package components.permissionsList;

import engine.PermissionRequestDTO;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;
import static util.Constants.GSON_INSTANCE;

public class PermissionsListRefresher  extends TimerTask  {
    private final Consumer<List<PermissionRequestDTO>> permissionListConsumer;
    private int requestNumber;
    private final BooleanProperty shouldUpdate;
    private String sheetName;

    public PermissionsListRefresher(BooleanProperty shouldUpdate, Consumer<List<PermissionRequestDTO>> permissionListConsumer, String sheetName) {
        this.shouldUpdate = shouldUpdate;
        this.permissionListConsumer = permissionListConsumer;
        this.sheetName = sheetName;
        requestNumber = 0;
    }

    @Override
    public void run() {
        if (!shouldUpdate.get() || sheetName == null || sheetName.isEmpty()) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;
        String requestUrl = Constants.PERMISSION_LIST_URL + "?sheetName=" + sheetName;

        HttpClientUtil.runAsync(requestUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
                try (response) {
                    if (!response.isSuccessful()) {
                        return;
                    }

                    String jsonArrayOfPermissions = response.body().string();
                    PermissionRequestDTO[] permissionArray = GSON_INSTANCE.fromJson(jsonArrayOfPermissions, PermissionRequestDTO[].class);
                    permissionListConsumer.accept(Arrays.asList(permissionArray));
                } catch (Exception e) {
                    System.out.println("Error processing response: " + e.getMessage());
                }
            }
        });
    }

    public void updateSheetName(String newSheetName) {
        this.sheetName = newSheetName;
    }
}

