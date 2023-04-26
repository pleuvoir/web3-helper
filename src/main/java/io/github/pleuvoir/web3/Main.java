package io.github.pleuvoir.web3;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        // 添加一个按钮
        Button importButton = new Button("导入");
        importButton.getStyleClass().add("button-raised");
        gridPane.add(importButton, 0, 0);

        Button eButton = new Button("导入");
        eButton.getStyleClass().add("button-raised");
        gridPane.add(eButton, 0, 1);


        // 添加一个文本域
        TextArea textArea = new TextArea();
        gridPane.add(textArea, 1, 0, 10, 10);

//        // 添加10列和10行的大小限制
//        for (int i = 1; i < 11; i++) {
//            // 添加列限制
//            ColumnConstraints column = new ColumnConstraints();
//            column.setPrefWidth(50);
//            gridPane.getColumnConstraints().add(column);
//
//            // 添加行限制
//            RowConstraints row = new RowConstraints();
//            row.setPrefHeight(30);
//            gridPane.getRowConstraints().add(row);
//        }

        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Grid Pane布局");
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}