package com.repsheet.librepsheet;

public class ActorStatus {
    private final String reason;
    private final Actor.Status status;

    public ActorStatus(Actor.Status status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public Actor.Status getStatus() {
        return status;
    }
}
