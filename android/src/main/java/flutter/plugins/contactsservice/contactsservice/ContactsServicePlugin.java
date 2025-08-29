package flutter.plugins.contactsservice.contactsservice;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class ContactsServicePlugin implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {
    private MethodChannel channel;
    private Activity activity;
    private ContentResolver contentResolver;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        channel = new MethodChannel(binding.getBinaryMessenger(), "github.com/contacts_service");
        channel.setMethodCallHandler(this);
        contentResolver = binding.getApplicationContext().getContentResolver();
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "getContacts":
                ContactsFetcher.fetchContacts(contentResolver, result, call);
                break;
            case "addContact":
                ContactAdder.addContact(activity, call, result);
                break;
            case "deleteContact":
                ContactDeleter.deleteContact(activity, call, result);
                break;
            case "updateContact":
                ContactUpdater.updateContact(activity, call, result);
                break;
            case "openContactForm":
                openContactForm(result);
                break;
            default:
                result.notImplemented();
        }
    }

    private void openContactForm(MethodChannel.Result result) {
        if (activity == null) {
            result.error("NO_ACTIVITY", "ContactsService requires a foreground activity.", null);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
        activity.startActivity(intent);
        result.success(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
    }
}
