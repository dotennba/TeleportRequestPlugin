package com.example.teleport;

import java.util.UUID;

public class TeleportRequest {
    public enum Type { TPI, TPC }

    private final UUID sender;
    private final UUID target;
    private final Type type;
    private final long timestamp;

    public TeleportRequest(UUID sender, UUID target, Type type) {
        this.sender = sender;
        this.target = target;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getSender() { return sender; }
    public UUID getTarget() { return target; }
    public Type getType() { return type; }
    public long getTimestamp() { return timestamp; }
}
