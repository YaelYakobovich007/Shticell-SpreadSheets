package components.header;

import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

public class IsVersionUpToDateRefresher extends TimerTask {

    private final BooleanProperty shouldUpdate;
    private final Consumer<Boolean> versionUpdateConsumer;

    public IsVersionUpToDateRefresher(BooleanProperty shouldUpdate, Consumer<Boolean> versionUpdateConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.versionUpdateConsumer = versionUpdateConsumer;
    }

    @Override
    public void run() {
        if (!shouldUpdate.get()) {
            return;
        }

        HttpClientUtil.runAsync(Constants.IS_VERSION_UP_TO_DATE, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Failed to check version: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string().trim() : "false";
                        boolean isUpToDate = Boolean.parseBoolean(responseBody);
                        versionUpdateConsumer.accept(isUpToDate);
                    } else {
                        System.out.println("Failed to check version: " + response.message());
                    }
                }
            }
        });
    }
}
