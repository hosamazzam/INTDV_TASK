package apps.hosamazzam.com.intdv_task.helpers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import static android.app.Activity.RESULT_OK;

public class IntentHelper {

    public static void goTo(@NonNull Activity current, @NonNull Class target, Bundle data) {
        Intent intent = new Intent(current, target);
        if (data != null) {
            intent.putExtras(data);
        }
        current.startActivity(intent);
    }

    public static void goTOAndFinish(@NonNull Activity current, @NonNull Class target, Bundle data) {
        Intent intent = new Intent(current, target);
        if (data != null) {
            intent.putExtras(data);
        }
        current.startActivity(intent);
        current.finish();
    }

    public static void goTOAndFinishAll(@NonNull Activity current, @NonNull Class target, Bundle data) {
        Intent intent = new Intent(current, target);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (data != null) {
            intent.putExtras(data);
        }
        current.startActivity(intent);
        current.finish();
    }

    public static void goToWithResult(Activity current, Class target, Bundle data, int requestCode) {
        Intent intent = new Intent(current, target);
        if (data != null) {
            intent.putExtras(data);
        }
        current.startActivityForResult(intent, requestCode);
    }

    public static void goToAction(@NonNull Activity current, @NonNull String intentType, @NonNull Uri uri) {
        current.startActivity(new Intent(intentType, uri));
    }

    public static void backAndFinish(@NonNull Activity activity, Bundle bundle) {
        Intent intent = new Intent();
        intent.putExtras(bundle);
        activity.setResult(RESULT_OK, intent);
        activity.finish();
    }
}
