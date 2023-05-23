package io.github.pleuvoir.web3.ui;

import io.github.pleuvoir.web3.utils.AccountHelper;
import java.io.File;
import java.math.BigDecimal;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Convert;

/**
 * @author <a href="mailto:pleuvior@foxmail.com">pleuvoir</a>
 */
public class AccountUI {

    private static Thread currentThread; // 当前任务所在的线程


    public static Pane create(Stage primaryStage) {

        // 创建一个垂直布局
        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);

        Tab tab = new Tab("My Tab");
        tab.setContent(root);

        // 创建一个水平布局用于放置按钮
        GridPane buttonBox = new GridPane();
        buttonBox.setHgap(10);
        buttonBox.setVgap(10);

        Label gasFeeLabel = new Label("GAS Fee: 0.0");
        buttonBox.add(gasFeeLabel, 0, 0);

        Button generateKeyButton = new Button("生成账户");
        generateKeyButton.getStyleClass().add("button-raised");
        buttonBox.add(generateKeyButton, 1, 0);

        Button importKeyButton = new Button("导入私钥");
        importKeyButton.getStyleClass().add("button-raised");
        buttonBox.add(importKeyButton, 2, 0);

        Button balanceButton = new Button("查询余额");
        balanceButton.getStyleClass().add("button-raised");
        buttonBox.add(balanceButton, 3, 0);

        Button stopButton = new Button("停止");
        stopButton.getStyleClass().add("button-raised");
        stopButton.setDisable(true); // 刚开始停止按钮不可点击
        buttonBox.add(stopButton, 4, 0);

        // 设置日志区域
        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        VBox.setVgrow(logArea, Priority.ALWAYS);

        // 将按钮和日志区域添加到垂直布局中
        root.getChildren().addAll(buttonBox, logArea);

        // 设置生成私钥的按钮点击事件
        generateKeyButton.setOnAction(e -> {

            // 选择保存路径
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("选择保存路径");
            File selectedDirectory = directoryChooser.showDialog(primaryStage);

            // 如果选择了路径则在后台线程中执行生成私钥的操作
            if (selectedDirectory != null) {
                Task<String> task = new Task<String>() {
                    @Override
                    protected String call() {
                        // 生成一个新的以太坊账户
                        return AccountHelper.generateNewWalletFile(selectedDirectory.getAbsolutePath());
                    }
                };

                // 创建一个进度条
                ProgressBar progressBar = new ProgressBar();
                VBox.setVgrow(progressBar, Priority.NEVER);
                root.getChildren().add(0, progressBar);

                // 监听任务状态变化
                task.setOnSucceeded(event -> {
                    String result = task.getValue();
                    if (result != null) {
                        // 在日志区域显示结果
                        logArea.appendText("任务执行成功，结果为：" + result + "\n");
                        // 弹出对话框显示结果
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "任务执行成功，结果为：" + result);
                        alert.showAndWait();
                    }
                    // 将进度条移除
                    root.getChildren().remove(progressBar);
                });
                task.setOnFailed(event -> {
                    // 在日志区域显示错误信息
                    logArea.appendText("任务执行失败，错误信息：" + task.getException().getMessage() + "\n");
                    // 将进度条移除
                    root.getChildren().remove(progressBar);
                });

                // 启动任务
                currentThread = new Thread(task);
                currentThread.setDaemon(true);
                currentThread.start();
                // 设置停止按钮可点击
                stopButton.setDisable(false);
                stopButton.setOnAction(event -> {
                    // 中断任务
                    if (currentThread != null && currentThread.isAlive()) {
                        currentThread.interrupt();
                        // 在日志区域显示结果
                        logArea.appendText("任务已取消\n");
                        // 将进度条移除
                        root.getChildren().remove(progressBar);
                        // 设置停止按钮不可点击
                        stopButton.setDisable(true);
                    }
                });
            }
        });

        // 设置导入私钥的按钮点击事件
        importKeyButton.setOnAction(e -> {

            // 选择私钥文件
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择私钥文件");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            // 如果选择了文件则在后台线程中执行导入私钥的操作
            if (selectedFile != null) {
                Task<Credentials> task = new Task<Credentials>() {
                    @Override
                    protected Credentials call() {
                        // 从文件中读取私钥
                        return AccountHelper.loadCredentials(selectedFile.getAbsolutePath());
                    }
                };

                // 创建一个进度条
                ProgressBar progressBar = new ProgressBar();
                VBox.setVgrow(progressBar, Priority.NEVER);
                root.getChildren().add(0, progressBar);

                // 监听任务状态变化
                task.setOnSucceeded(event -> {
                    Credentials result = task.getValue();
                    if (result != null) {
                        // 在日志区域显示结果
                        logArea.appendText("任务执行成功，地址为：" + result.getAddress() + "\n");
                        // 弹出对话框显示结果
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "任务执行成功，地址为：" + result.getAddress());
                        alert.showAndWait();
                    }
                    // 将进度条移除
                    root.getChildren().remove(progressBar);
                });
                task.setOnFailed(event -> {
                    // 在日志区域显示错误信息
                    logArea.appendText("任务执行失败，错误信息：" + task.getException().getMessage() + "\n");
                    // 将进度条移除
                    root.getChildren().remove(progressBar);
                });

                // 启动任务
                currentThread = new Thread(task);
                currentThread.setDaemon(true);
                currentThread.start();
                // 设置停止按钮可点击
                stopButton.setDisable(false);
                stopButton.setOnAction(event -> {
                    // 中断任务
                    if (currentThread != null && currentThread.isAlive()) {
                        currentThread.interrupt();
                        // 在日志区域显示结果
                        logArea.appendText("任务已取消\n");
                        // 将进度条移除
                        root.getChildren().remove(progressBar);
                        // 设置停止按钮不可点击
                        stopButton.setDisable(true);
                    }
                });
            }
        });

        // 设置查询余额的按钮点击事件
        balanceButton.setOnAction(e -> {

            // 选择账户地址
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("输入账户地址");
            dialog.setHeaderText("请输入要查询的账户地址：");
            dialog.setContentText("地址：");
            dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(true);
            dialog.getDialogPane().lookupButton(ButtonType.CANCEL).addEventFilter(ActionEvent.ACTION, event -> {
                // 中断任务
                if (currentThread != null && currentThread.isAlive()) {
                    currentThread.interrupt();
                    // 在日志区域显示结果
                    logArea.appendText("任务已取消\n");
                    // 设置停止按钮不可点击
                    stopButton.setDisable(true);
                }
            });

            String address = dialog.showAndWait().orElse(null);

            // 如果输入了地址则在后台线程中执行查询余额的操作
            if (address != null && !address.isEmpty()) {
                Task<BigDecimal> task = new Task<BigDecimal>() {
                    @Override
                    protected BigDecimal call() {
                        return AccountHelper.getBalance(address);
                    }
                };
                // 创建一个进度条
                ProgressBar progressBar = new ProgressBar();
                VBox.setVgrow(progressBar, Priority.NEVER);
                root.getChildren().add(0, progressBar);

                // 监听任务状态变化
                task.setOnSucceeded(event -> {
                    BigDecimal result = task.getValue();
                    if (result != null) {
                        // 在日志区域显示结果
                        logArea.appendText("任务执行成功，余额为：" + Convert.fromWei(result, Convert.Unit.ETHER) + " ETH\n");
                        // 弹出对话框显示结果
                        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                                "任务执行成功，余额为：" + Convert.fromWei(result, Convert.Unit.ETHER) + " ETH");
                        alert.showAndWait();
                    }
                    // 将进度条移除
                    root.getChildren().remove(progressBar);
                });
                task.setOnFailed(event -> {
                    // 在日志区域显示错误信息
                    logArea.appendText("任务执行失败，错误信息：" + task.getException().getMessage() + "\n");
                    // 将进度条移除
                    root.getChildren().remove(progressBar);
                    // 设置停止按钮不可点击
                    stopButton.setDisable(true);
                });

                // 启动任务
                currentThread = new Thread(task);
                currentThread.setDaemon(true);
                currentThread.start();
                // 设置停止按钮可点击
                stopButton.setDisable(false);
                stopButton.setOnAction(event -> {
                    // 中断任务
                    if (currentThread != null && currentThread.isAlive()) {
                        currentThread.interrupt();
                        // 在日志区域显示结果
                        logArea.appendText("任务已取消\n");
                        // 将进度条移除
                        root.getChildren().remove(progressBar);
                        // 设置停止按钮不可点击
                        stopButton.setDisable(true);
                    }
                });
            }
        });

        // 新建一个ScheduledService
        ScheduledService<BigDecimal> gasPriceService = new ScheduledService<BigDecimal>() {
            @Override
            protected Task<BigDecimal> createTask() {
                return new Task<BigDecimal>() {
                    @Override
                    protected BigDecimal call() {
                        return AccountHelper.getGasPrice();
                    }
                };
            }
        };

        // 每5秒更新一次GAS费用
        gasPriceService.setPeriod(Duration.seconds(5));

        // 监听服务状态变化
        gasPriceService.setOnSucceeded(event -> {
            BigDecimal gasPrice = gasPriceService.getValue();
            if (gasPrice != null) {
                // 在日志区域显示结果
                gasFeeLabel.setText("GAS Fee: " + gasPrice);
            } else {
                // 在日志区域显示错误信息
                gasFeeLabel.setText("查询GAS费用失败\n");
            }
        });
        gasPriceService.setOnFailed(event -> {
            // 在日志区域显示错误信息
            gasFeeLabel.setText("查询GAS费用失败\n");
        });

        // 启动服务
        gasPriceService.start();

        return root;

    }
}
