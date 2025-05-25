package toni.jtn.foundation.events.util;

import lombok.Getter;
import lombok.Setter;

/**
 * The base of an event that may be cancelled.
 */
@Setter
@Getter
public abstract class BaseEvent {
    public enum Result {
        DENY,
        DEFAULT,
        ALLOW
    }

    private Result result = Result.DEFAULT;
    private boolean canceled;

    public abstract void sendEvent();

    public boolean post() {
        sendEvent();
        return isCanceled();
    }
}