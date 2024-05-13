package com.mint.lc.demo;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public interface Contractor {

    interface View {
        Background BACKGROUND = new Background(new BackgroundFill(
                Color.BLACK,
                new CornerRadii(0),
                new Insets(0)
        ));
        Font DEFAULT_FONT = Font.font("Arial", 14);

        Scene build();
    }

    interface Presenter {
    }

    interface Model<T> {

        String API_URL_PROPERTY = "api.url";

        T getValue();

        Throwable getException();
    }
}
