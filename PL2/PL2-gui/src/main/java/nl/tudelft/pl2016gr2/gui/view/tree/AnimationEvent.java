package nl.tudelft.pl2016gr2.gui.view.tree;

import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.util.Duration;

/**
 *
 * @author Faris
 */
public class AnimationEvent extends Event {

    public static final EventType<AnimationEvent> ANIMATION_EVENT = new EventType(ANY, "ANIMATION");
    private final Timeline timeline;
    private final Duration duration;
    private final double startX, startY, endX, endY, scale;

    public AnimationEvent(double startX, double startY, double endX, double endY, double scale,
            Timeline timeline, Duration duration) {
        super(ANIMATION_EVENT);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.scale = scale;
        this.timeline = timeline;
        this.duration = duration;
    }

    public double getScale() {
        return scale;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getEndX() {
        return endX;
    }

    public double getEndY() {
        return endY;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public Duration getDuration() {
        return duration;
    }
}
