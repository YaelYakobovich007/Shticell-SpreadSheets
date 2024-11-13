package components.header;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VersionComboBoxRefresher extends TimerTask {

    private final BooleanProperty shouldUpdate;
    private final Consumer<ObservableList<Integer>> versionUpdateConsumer;

    public VersionComboBoxRefresher(BooleanProperty shouldUpdate, Consumer<ObservableList<Integer>> versionUpdateConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.versionUpdateConsumer = versionUpdateConsumer;
    }

    @Override
    public void run() {
        if (!shouldUpdate.get()) {
            return;
        }

        HttpClientUtil.runAsync(Constants.GET_SHEET_VERSION_COUNT_URL, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Failed to get version count: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "1";
                        responseBody = responseBody.trim();
                        int versionCount = Integer.parseInt(responseBody);

                        List<Integer> versions = IntStream.rangeClosed(1, versionCount)
                                .boxed().collect(Collectors.toList());
                        Platform.runLater(() -> versionUpdateConsumer.accept(FXCollections.observableList(versions)));
                    } else {
                        System.out.println("Failed to get version count: " + response.message());
                    }
                }
            }
        });
    }
}
