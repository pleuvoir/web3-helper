package io.github.pleuvoir.web3;

import io.github.pleuvoir.web3.config.AppConfig;
import io.github.pleuvoir.web3.ui.AccountUI;
import io.github.pleuvoir.web3.ui.ZksyncUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Bootstrap extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        this.initConfig();

        TabPane tabPane = new TabPane();

        tabPane.getTabs().add(this.account(primaryStage));
        tabPane.getTabs().add(this.zksyncEra(primaryStage));


        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("以太坊学习笔记");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpeg")));

        primaryStage.show();
    }




    private Tab account(Stage primaryStage) {
        Tab tab = new Tab("账户操作");
        tab.setClosable(false);
        tab.setContent(AccountUI.create(primaryStage));
        return tab;
    }


    private Tab zksyncEra(Stage primaryStage) {
        Tab tab = new Tab("zksync交互");
        tab.setClosable(false);
        tab.setContent(ZksyncUI.create(primaryStage));
        return tab;
    }


    public static void main(String[] args) {
        launch(args);
    }


    private void initConfig(){
        AppConfig.init();
    }
}