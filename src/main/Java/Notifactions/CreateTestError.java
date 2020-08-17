package Notifactions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class CreateTestError {
    private static final NotificationGroup error = new NotificationGroup("Test Creation Error", NotificationDisplayType.BALLOON, true);

    public static Notification notify(String content) {
        return notify(null, content);
    }

    public static Notification notify(Project project, String content) {
        final Notification notification = error.createNotification(content, NotificationType.ERROR);
        notification.notify(project);
        return notification;
    }
}
