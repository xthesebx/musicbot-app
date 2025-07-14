package com.seb.musicapp.common;

public class Server {
  private String serverName;
  private String UUID;

  public Server(String serverName, String UUID) {
    this.serverName = serverName;
    this.UUID = UUID;
  }

  public String getServerName() {
    return serverName;
  }

  public String getUUID() {
    return UUID;
  }

  @Override
  public String toString() {
    return serverName;
  }
}
