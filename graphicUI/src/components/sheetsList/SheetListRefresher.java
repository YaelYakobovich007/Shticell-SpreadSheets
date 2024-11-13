package components.sheetsList;

import engine.SheetDetailsDTO;
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

public class SheetListRefresher extends TimerTask {

    private final Consumer<List<SheetDetailsDTO>> sheetsListConsumer;
    private int requestNumber;
    private final BooleanProperty shouldUpdate;

    public SheetListRefresher(BooleanProperty shouldUpdate, Consumer<List<SheetDetailsDTO>> sheetsListConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.sheetsListConsumer = sheetsListConsumer;
        requestNumber = 0;
    }

    @Override
    public void run() {
        if (!shouldUpdate.get()) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;
        HttpClientUtil.runAsync(Constants.SHEETS_LIST_URL, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfSheets = response.body().string(); // Read the response body only once


                SheetDetailsDTO[] sheetDetailsArray = GSON_INSTANCE.fromJson(jsonArrayOfSheets, SheetDetailsDTO[].class);
                sheetsListConsumer.accept(Arrays.asList(sheetDetailsArray));
            }

        });
    }

}