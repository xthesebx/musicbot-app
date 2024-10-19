package Discord;


import Discord.OS.ExecutorManager;
import Discord.OS.OperatingSystem;
import Discord.OS.SystemManager;
import com.hawolt.logger.Logger;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.LogLevel;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityButton;
import de.jcm.discordgamesdk.activity.ActivityButtonsMode;
import de.jcm.discordgamesdk.activity.ActivityType;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

public class DiscordActivity implements Runnable {

    private final Activity activity;
    private ActivityButton button, join;

    public static Optional<DiscordActivity> create() {
        String processName = switch (OperatingSystem.getOperatingSystemType()) {
            case MAC -> "Discord.app";
            case LINUX -> "Discord";
            case WINDOWS -> "Discord.exe";
            default -> null;
        };
        try {
            if (processName == null || !SystemManager.getInstance().isProcessRunning(processName)) {
                return Optional.empty();
            }
        } catch (IOException e) {
            Logger.error(e);
        }
        return Optional.of(new DiscordActivity());
    }

    private Core core;

    private DiscordActivity() {
        ExecutorService loader = ExecutorManager.getService("rpc-loader");
        loader.execute(this);
        loader.shutdown();
        activity = new Activity();
    }

    @Override
    public void run() {
        try {
            try (CreateParams params = new CreateParams()) {
                params.setClientID(1178647694923792404L);
                params.setFlags(CreateParams.getDefaultFlags());
                try {
                    this.core = new Core(params);
                    this.core.setLogHook(LogLevel.ERROR, (logLevel, s) -> Logger.error("[discord-rpc] if rpc is working this can be ignored: {}", s));
                    this.setIdlePresence();
                    while (true) {
                        core.runCallbacks();
                        try {
                            Thread.sleep(16);
                        } catch (InterruptedException ignored) {
                        }
                    }
                } catch (Exception e) {
                    Logger.error(e);
                } finally {
                    if (core != null) core.close();
                }
            }
        } catch (Exception e) {
            Logger.error("[discord-rpc] an error has occurred");
            Logger.error(e);
        }
    }

    public void setIdlePresence() {
        if (join != null) activity.removeButton(join);
        this.set("Currently No song Playing", null, Instant.now(), null, null, false);
    }

    public void set(String details, String state, int duration, String url, boolean instance) {
        this.set(details, state, null, Instant.now().plusSeconds(duration), url, instance);
    }

    public void set(String details, String state, Instant start, Instant end, String url, boolean instance) {
        //TODO: add buttons as soon as they work i guess lol
        //opened issue https://github.com/JnCrMx/discord-game-sdk4j/issues/92
        activity.assets().setLargeImage("musicbotlogo");
        if (url != null) {
            if (button != null)
                activity.removeButton(button);
            //System.out.println(url);
            button = new ActivityButton("Songlink", url);
            activity.addButton(button);
            activity.setActivityButtonsMode(ActivityButtonsMode.BUTTONS);

        }
        activity.setType(ActivityType.LISTENING);
        activity.setDetails(details);
        activity.setState(state);
        activity.setInstance(instance);
        if (start != null && end == null) activity.timestamps().setStart(start);
        if (end != null && start == null) {
            activity.timestamps().setEnd(end);
        }
        try {
            core.activityManager().updateActivity(activity);
        } catch (RuntimeException e) {
            Logger.error(e);
        }
    }

    public void addJoin(String url) {
        if (join == null) join = new ActivityButton("Join Channel", url);
        else join.setUrl(url);
        activity.addButton(join);
        activity.setActivityButtonsMode(ActivityButtonsMode.BUTTONS);
    }
}
