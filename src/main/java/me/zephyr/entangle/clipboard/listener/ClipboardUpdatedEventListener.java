package me.zephyr.entangle.clipboard.listener;

import me.zephyr.entangle.clipboard.event.ClipboardUpdatedEvent;
import org.springframework.context.event.EventListener;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@EventListener(ClipboardUpdatedEvent.class)
public @interface ClipboardUpdatedEventListener {
}
