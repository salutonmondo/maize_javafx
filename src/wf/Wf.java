/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import wf.bean.Reflection;
import wf.bean.RegistrationInformation;
import wf.database.DataBaseUtility;

import static wf.database.DataBaseUtility.getRunTimePath;

import wf.database.ParamsUtil;
import wf.poi.PoiUtil;
import wf.print.PrintServiceApp;
import wf.service.RegistrationService;
import javafx.scene.paint.Color;
import wf.database.DesUtils;
import wf.database.LogUtil;
import wf.util.MultiSelectTableView;

/**
 * @author wadeshu
 */
public class Wf extends Application {

    public boolean needPoint = false;
    @FXML
    public GridPane gpInfo;
    @FXML
    ImageView imgTitle;
    TableView tableUserInfo;
    WfController controller;
    @FXML
    Button btnAdd;
    @FXML
    Button btnSave;
    @FXML
    Button btnPrint;
    @FXML
    TextField tfShortCut;
    @FXML
    Pagination pagination;

    @FXML
    TextField barCodeScan;

    @FXML
    Label scanResult;

    @FXML
    Label labelCount;
    int pageSize = 15;
    List<RegisterInfo> registerInfoList;
    HashMap<String, RegisterInfo> registerInfoMap;
    FileChooser fileChooser = new FileChooser();

    @FXML
    MenuItem excelImport;
    @FXML
    MenuItem excelExport;
    @FXML
    MenuItem settingServer;
    @FXML
    MenuItem menuitemHost;
    @FXML
    VBox waitingVbox;

    @FXML
    BorderPane mainBorder1;

    @FXML
    HBox manipulationHbox;

    @FXML
    HBox saveHbox;

    @FXML
    BorderPane titleBp;
    Stage primartyStage;
    MenuItem menuItemPrint;
    MenuItem menuItemEdit;
    MenuItem menuItemDelete;

    RegistrationInformation edittingRegInfo;
    ScheduledExecutorService executor;
    PrintServiceApp psa;

    int gpInfoColumn = 2;

    int currentMode;
    boolean firstChangeMode = true;

    @Override
    public void start(Stage primaryStage) {
        LogUtil.log("ok let's go!!!!!");
//        DataBaseUtility.startDBServer();
        this.primartyStage = primaryStage;
        Scene scene = new Scene(loadFxml());
        primaryStage.setTitle("中国辐射防护学会2017年学术年会");
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.getStylesheets().add("wf/wf.css");
        bindKey(scene);
    }

    private void bindKey(Scene scene) {
        changeMode(0);
        final KeyCombination keyComb1 = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (keyComb1.match(event)) {
                System.out.println("Ctrl+R pressed");
                changeMode(1);
            }
        });
    }

    private void changeMode(int mode) {
        currentMode = mode;
        if (mode == 0) {
            //pagination part;
            mainBorder1.getCenter().setManaged(false);
            mainBorder1.getCenter().setVisible(false);

            manipulationHbox.setVisible(false);
            manipulationHbox.setManaged(false);

            gpInfo.getColumnConstraints().get(0).setPercentWidth(50);
            gpInfo.getColumnConstraints().get(1).setPercentWidth(50);
            gpInfo.getColumnConstraints().get(2).setPrefWidth(0);
            gpInfo.getColumnConstraints().get(3).setPrefWidth(0);

            gpInfo.setVgap(25);

            ImageView img = new ImageView();
            img.setPreserveRatio(true);
            img.setFitHeight(250);
            img.setFitWidth(1366);
            Image image = new Image("wf/logo.jpg", 1366, 250, false, false);
            img.setImage(image);
            titleBp.setCenter(img);
            mainBorder1.setMargin(gpInfo, new Insets(15, 0, 0, 0));

            titleBp.getTop().setManaged(false);
            titleBp.getTop().setVisible(false);
        } else {
            gpInfoColumn = 4;
            titleBp.setCenter(labelCount);
            mainBorder1.getCenter().setManaged(true);
            mainBorder1.getCenter().setVisible(true);

            manipulationHbox.setVisible(true);
            manipulationHbox.setManaged(true);
//            GridPane.setConstraints(saveHbox, 1, 5); // column=3 row=1

            gpInfo.getColumnConstraints().get(0).setPercentWidth(25);
            gpInfo.getColumnConstraints().get(1).setPercentWidth(25);
            gpInfo.getColumnConstraints().get(2).setPrefWidth(25);
            gpInfo.getColumnConstraints().get(3).setPrefWidth(25);

            mainBorder1.setMargin(gpInfo, new Insets(10, 0, 0, 0));
            titleBp.getTop().setManaged(true);
            titleBp.getTop().setVisible(true);
            registerInfoMap.get("注册费用").tfValue.setEditable(true);
        }
        gpInfo.getChildren().clear();
        loadRegistrationInfo(false);
        addListener();
    }

    private Parent loadFxml() {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = Wf.class.getResourceAsStream("main2.fxml");
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Wf.class.getResource("main2.fxml"));
        loader.setController(this);
        Parent page = null;
        try {
            page = loader.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return page;
    }

    public void loadRegistrationInfo(boolean editting) {
        int i = 0;
        if (!editting) {
            tableUserInfo = new TableView<RegistrationInformation>();
            if (registerInfoList == null) {
                registerInfoList = new ArrayList<>();
            }
            if (registerInfoMap == null) {
                registerInfoMap = new HashMap<>();
            }
        }
        Class<RegistrationInformation> clazz = RegistrationInformation.class;
        Field[] infoFields = clazz.getDeclaredFields();
        int gpInfoRowIndex = 0;
        ArrayList<String> extraArr = new ArrayList<>();
        extraArr.add("id");
        extraArr.addAll(Arrays.asList(RegistrationInformation.arr));
        for(String fieldName:extraArr){
//        for (Field field : infoFields) {
            Field field = null;
            try{
               field= RegistrationInformation.class.getDeclaredField(fieldName);
            }catch(Exception e){
                e.printStackTrace();
            }
            System.out.println("eeeeeee:"+field);
            String name = field.getName();
            Reflection anno = field.getAnnotation(Reflection.class);
            boolean inDb = anno.existsInDb();
            if ("id".equals(name) && !editting) {
                addUserInfoColumn("".equals(anno.displayName())?name:anno.displayName(), anno.columWidth());
                continue;
            } else if ("arr".equals(name) || "identityType".equals(name)) {
                continue;
            }
            boolean necessary = true;
            field.setAccessible(true);
            if (anno != null) {
                necessary = anno.isNecessary() && anno.display();
                if (anno.onlyInTable()) {
                    continue;
                }
            }
            if (editting) {
                try {
                    if (field.get(edittingRegInfo) != null && registerInfoMap.get(name) != null) {
                        System.out.println("kkkkk:" + name + "    " + field.get(edittingRegInfo));
                        registerInfoMap.get(name).setValue(field.get(edittingRegInfo).toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                RegisterInfo regInfo = null;
                if (registerInfoMap != null) {
                    regInfo = registerInfoMap.get(name);
                }
                if (regInfo == null) {
                    regInfo = new RegisterInfo(name, necessary, registerInfoMap, currentMode, Wf.this, anno);
                    registerInfoList.add(regInfo);
                }
                System.out.println(name + " added!!!!");
                registerInfoMap.put(name, regInfo);
//                if (anno.dependent()) {
//                    continue;
//                }
                if (currentMode == 0 && anno.customNotAloowed()) {
                    continue;
                }
                if (!anno.display()) {
                    continue;
                }
                int column = i % gpInfoColumn;
                int row = i / gpInfoColumn;
                gpInfo.add(regInfo, column, row);
                gpInfoRowIndex = row;
                gpInfo.setHgap(15);
                gpInfo.setVgap(15);
                System.out.println("xxxxx:"+row+":"+column);
                if (inDb) {
//                    addUserInfoColumn(name, anno.columWidth());
                     addUserInfoColumn("".equals(anno.displayName())?name:anno.displayName(), anno.columWidth());
                }
                i++;
            }
        }
        if (!editting) {
            gpInfo.add(saveHbox, 0, gpInfoRowIndex + 1);
            if (currentMode == 1) {
//                gpInfo.addRow(gpInfoRowIndex + 1);
                GridPane.setConstraints(saveHbox, 1, gpInfoRowIndex + 1, 2, 1);
            }
        }
        if (currentMode == 0) {
            registerInfoMap.get("报名类型").choiceBoxType.getItems().addAll(RegisterInfo.typs1);
            registerInfoMap.get("报名类型").choiceBoxType.getSelectionModel().select("现场新增");
        } else if (firstChangeMode) {
            firstChangeMode = false;
            RegisterInfo regInfo = registerInfoMap.get("报名类型");
            regInfo.choiceBoxType.getItems().clear();
            regInfo.choiceBoxType.getItems().addAll(RegisterInfo.typs);
            regInfo.choiceBoxType.getSelectionModel().select(regInfo.getValue());
        }
        if (currentMode == 0) {
            btnAdd.setVisible(false);
            btnAdd.setManaged(false);
            btnSave.setText("提交注册");
        } else {
            btnAdd.setVisible(true);
            btnAdd.setManaged(true);
            btnSave.setText("保存");
        }

    }

    private Node loadRegistration(String searchString, int page) {
        if (searchString.equals("")) {
            searchString = "'%'";
        } else {
            searchString = "'%" + searchString + "%'";
        }
        String querySql = "select * from registration where pinyin like " + searchString + " or 手机号 like " + searchString
                + " or 工作单位 like " + searchString
                + " or 姓名 like " + searchString
                + " or 邮箱 like " + searchString
                + " or 是否付费 like " + searchString
                + " order by id desc " + " limit " + pageSize + " offset " + (page - 1) * pageSize;
        ObservableList<RegistrationInformation> dataList = FXCollections.observableArrayList();
        System.out.println(querySql);
        try {
            Connection conn = DataBaseUtility.getConnection();
            PreparedStatement stm = conn.prepareStatement(querySql);
            ResultSet rs = stm.executeQuery();
            Field[] fields = RegistrationInformation.class.getDeclaredFields();
            while (rs.next()) {
                RegistrationInformation object = new RegistrationInformation();
                for (Field field : fields) {
                    Reflection ref = field.getAnnotation(Reflection.class);
                    if (ref != null) {
                        if (!ref.existsInDb()) {
                            continue;
                        }
                    }
                    field.setAccessible(true);
                    field.set(object, rs.getString(field.getName()));
                }
                dataList.add(object);
            }
            tableUserInfo.setItems(dataList);
            tableUserInfo.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            tableUserInfo.setRowFactory(new Callback<TableView<RegistrationInformation>, TableRow<RegistrationInformation>>() {
                @Override
                public TableRow<RegistrationInformation> call(TableView<RegistrationInformation> tableView2) {
                    final TableRow<RegistrationInformation> row = new TableRow<>();
                    row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            final int index = row.getIndex();
                            if (index >= 0 && index < tableUserInfo.getItems().size() && tableUserInfo.getSelectionModel().isSelected(index)
                                    && !event.isSecondaryButtonDown()
                                    ) {
                                tableUserInfo.getSelectionModel().clearSelection();
                                event.consume();
                            }
                        }
                    });
                    return row;
                }
            });
            conn.close();
            return new BorderPane(tableUserInfo);
        } catch (Exception ex) {
            Logger.getLogger(Wf.class.getName()).log(Level.SEVERE, null, ex);
            LogUtil.log(ex.getMessage());
        }
        return null;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void addUserInfoColumn(String name, int width) {
        TableColumn tableColumn = new TableColumn(name);
        tableColumn.setCellValueFactory(new PropertyValueFactory(name));
        tableColumn.prefWidthProperty().bind(tableUserInfo.widthProperty().multiply(width * 1.0d / 1000));
        tableUserInfo.getColumns().add(tableColumn);
    }

    private void addListener() {
        EventHandler<MouseEvent> me = (MouseEvent event) -> {
            Object source = event.getSource();
            if (source == btnAdd) {
                clearRegisterInfos();
                edittingRegInfo = null;
            } else if (source == btnSave) {
                try {
                    RegistrationService service = new RegistrationService(this);
                    for (RegisterInfo info : registerInfoList) {
                        if (info.necessary) {
                            boolean valid = true;
                            String errorInfo = "";
                            if (info.name.equals("邮箱")) {
                                valid = info.isValidEmailAddress(info.getValue());
                                errorInfo = "邮箱格式不正确";
                            } else if (info.name.equals("手机号")) {
                                valid = info.isChinaPhoneLegal(info.getValue());
                                errorInfo = "请检查手机号码";
                            } else if ((info.name.equals("职称") || info.name.equals("学历"))) {
                                if (needPoint) {
                                    valid = !info.choiceBoxType.getSelectionModel().getSelectedItem().toString().equals("");
                                    errorInfo = "需要学分必须填写" + info.name;
                                }
                            } else if (info.name.equals("职务")) {
                                if (needPoint) {
                                    valid = info.getValue() != null && !info.getValue().equals("");
                                    errorInfo = "需要学分必须填写" + info.name;
                                }
                            } else {
                                valid = info.getValue().length() > 0;
                                errorInfo = info.name + "是必填项";
                            }
                            if (!valid) {
                                Alert alert = new Alert(AlertType.INFORMATION);
                                alert.setTitle("");
                                alert.setHeaderText(null);
                                alert.setContentText(errorInfo);
                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get() == ButtonType.OK) {
                                    info.tfValue.requestFocus();
                                } else {
                                }
                                return;
                            }
                        }

                    }
                    if (edittingRegInfo == null) {
                        boolean result = service.saveRegisterInfos(RegistrationInformation.class, registerInfoMap, null);
                        if (result) {
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("注册成功");
                            alert.setHeaderText(null);
                            alert.setContentText("恭喜您,注册成功!");
                            alert.showAndWait();
                        }
                    } else {
                        RegistrationService.updateRegistration(edittingRegInfo.getId(), registerInfoMap);
                    }
                    edittingRegInfo = null;
                    clearRegisterInfos();
                    refreshPage();
                    tableUserInfo.getSelectionModel().selectFirst();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (source == btnPrint) {
                printSelection();
            } else if (source == labelCount) {
                Wf.this.refreshLabelCount();
            }
        };
        EventHandler<ActionEvent> ah = (ActionEvent event) -> {
            Object source = event.getSource();
            if (source == excelImport) {
                File file = fileChooser.showOpenDialog(primartyStage);
                if (file != null) {
                    PoiUtil.importExcel(file);
                    refreshPage();
                }
            } else if (source == excelExport) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("精神分析大会数据(*.xlsx)", "*.xlsx"));
                fileChooser.setTitle("选择保存文件");
                File file = fileChooser.showSaveDialog(primartyStage);
                if (file != null) {
                    PoiUtil.exportExcel(file);
                }
            } else if (source == settingServer) {
                HashMap<String, String> params = ParamsUtil.loadParams();
                TextInputDialog dialog = new TextInputDialog(params.get("ServerAddress"));
                dialog.setTitle("设置服务器地址");
                dialog.setHeaderText("服务器IP");
                dialog.setContentText("content");
                dialog.setContentText("");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    params.put("ServerAddress", result.get());
                    ParamsUtil.saveParamChanges(params);
                    refreshPage();
                }
            } else if (source == menuitemHost) {
                DataBaseUtility.startDBServer();
            } else if (source == menuItemDelete) {
                RegistrationInformation regInfo = (RegistrationInformation) tableUserInfo.getSelectionModel().getSelectedItem();
                RegistrationService.updatePay(regInfo.getId());
                refreshPage();
            } else if (source == menuItemEdit) {
                edittingRegInfo = (RegistrationInformation) tableUserInfo.getSelectionModel().getSelectedItem();
                showHideFields("是".equals(edittingRegInfo.get是否需要学分()));
//                registerInfoMap.get("是否需要学分").groupSex.selectToggle(registerInfoMap.get("是否需要学分").sexMale);
                loadRegistrationInfo(true);
            } else if (source == menuItemPrint) {
                printSelection();
            }
        };

        btnAdd.setOnMouseClicked(me);
        btnSave.setOnMouseClicked(me);
        btnPrint.setOnMouseClicked(me);
//        tfShortCut.textProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue.length() == 0 || newValue.length() > 1) {
//                refreshPage();
//                tableUserInfo.getSelectionModel().selectFirst();
//            }
//        });
        tfShortCut.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    refreshPage();
                    tableUserInfo.getSelectionModel().selectFirst();
                }
            }
        });
        labelCount.setOnMouseClicked(me);
        excelImport.setOnAction(ah);
        excelExport.setOnAction(ah);
        settingServer.setOnAction(ah);
        menuitemHost.setOnAction(ah);
        refreshPage();

        /**
         * context menu
         */
        ContextMenu cm = new ContextMenu();
        menuItemPrint = new MenuItem("打印");
        menuItemEdit = new MenuItem("编辑");
        menuItemDelete = new MenuItem("成功付费");
        menuItemPrint.setOnAction(ah);
        menuItemEdit.setOnAction(ah);
        menuItemDelete.setOnAction(ah);
        cm.getItems().add(menuItemPrint);
        cm.getItems().add(menuItemEdit);
        cm.getItems().add(menuItemDelete);
        tableUserInfo.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                System.out.println("right mouse clicked!");
                cm.show(pagination, e.getScreenX(), e.getScreenY());
                e.consume();
            } else {
                cm.hide();
            }
        });
        barCodeScan.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    try {
                        String newValue = barCodeScan.getText();
                        System.out.println("id::::::" + newValue);
//                if (newValue.contains("\n")) {
                        DesUtils des = new DesUtils("hbue");//自定义密钥  
                        String id = des.decrypt(newValue).replace("maizehuizhan", "");
                        System.out.println("id::::::" + id);
                        String currentQuery = "select count(id) from registration where 已领资料 = false and 是否付费 = '是' and id='" + id + "'";
                        System.out.println("sql::::::" + currentQuery);
                        int result = queryForIntResult(currentQuery);
                        if (result == 1) {
                            scanResult.setText("领取成功!");
                            scanResult.setTextFill(Color.GREEN);
                            RegistrationService.scanSuccess(id);
                            Wf.this.refreshLabelCount();
                        } else {
                            scanResult.setText("领取失败");
                            scanResult.setTextFill(Color.RED);
                        }
                        barCodeScan.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                        scanResult.setText("非法条码！");
                        scanResult.setTextFill(Color.RED);
                        barCodeScan.setText("");
                    }
                }
            }
        });
    }

    class ClearTask extends TimerTask {

        @Override
        public void run() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

    }

    private void printRegInfo(final RegistrationInformation regInfo) {
        Platform.runLater(() -> {
              psa.printOrder(regInfo);
        });
    }

    public void showHideFields(boolean show) {
        int gpInfoRowIndex = 0;
        gpInfo.getChildren().clear();
        int i = 0;
        Field[] infoFields = RegistrationInformation.class.getDeclaredFields();
        for (Field field : infoFields) {
            String name = field.getName();
            Reflection anno = field.getAnnotation(Reflection.class);
            if ("id".equals(name)) {
//                addUserInfoColumn(name, anno.columWidth());
                continue;
            } else if ("arr".equals(name) || "identityType".equals(name)) {
                continue;
            }
            if (anno.dependent() && !show) {
                continue;
            }
            int column = i % gpInfoColumn;
            int row = i / gpInfoColumn;
            gpInfoRowIndex = row;
            if (registerInfoMap.get(name) == null) {
                continue;
            }
            gpInfo.add(registerInfoMap.get(name), column, row);
            gpInfo.setHgap(15);
            gpInfo.setVgap(15);
            i++;
        }
        gpInfo.add(saveHbox, 0, gpInfoRowIndex + 1);
        if (currentMode == 1) {
            GridPane.setConstraints(saveHbox, 1, gpInfoRowIndex + 1, 2, 1);
        }
    }

    private void refreshPage() {
        showWaiting();
        new Thread(() -> {
            try {
                refreshPageImple();
                refreshLabelCount();
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    hideWaiting();
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("系统错误");
                    alert.setHeaderText(null);
//                            alert.setContentText("数据库连接失败，请设置服务器地址");
                    alert.setContentText("file:" + getRunTimePath() + "/lib/wf" + "\n" + ex.getMessage());
                    alert.showAndWait();
                });
            }
        }).start();
    }

    int pageCount;

    private void refreshPageImple() throws Exception {
        pageCount = getPageCount(tfShortCut.getText());
        System.out.println("pageCount:" + pageCount);
        if (pageCount == 0) {
            pageCount = 1;
        }
        Platform.runLater(() -> {
            pagination.setPageCount(pageCount);
            pagination.setPageFactory(pageIndex -> {
                Node node = loadRegistration(tfShortCut.getText(), pageIndex + 1);
                System.out.println("node return success" + node);
                return node;
            });
            pagination.setCurrentPageIndex(0);
            hideWaiting();
        });
    }

    private void clearRegisterInfos() {
        for (RegisterInfo regInfo : registerInfoList) {
            regInfo.clearInfo();
        }
        for (Map.Entry<String, RegisterInfo> ent : registerInfoMap.entrySet()) {
            ent.getValue().clearInfo();
        }
    }

    private int getPageCount(String searchString) throws Exception {
        int pageCount = 1;
        int recordsCount = 0;
        if (searchString.equals("")) {
            searchString = "'%'";
        } else {
            searchString = "'%" + searchString + "%'";
        }
        String countString = "select count(id) from registration where pinyin like " + searchString + " or 手机号 like " + searchString;
        recordsCount = queryForIntResult(countString);
        if (recordsCount % pageSize == 0) {
            pageCount = recordsCount / pageSize;
        } else {
            pageCount = recordsCount / pageSize + 1;
        }
        return pageCount;
    }

    int total = 0, getPaper = 0, printed = 0;

    private void refreshLabelCount() {
        String totalQuery = "select count(id) from registration";
        String getPaperSql = "select count(id) from registration where 已领资料 = true ";
        String printedSql = "select count(id) from registration where 已打印胸牌 = true ";
        total = queryForIntResult(totalQuery);
        getPaper = queryForIntResult(getPaperSql);
        printed = queryForIntResult(printedSql);
        Platform.runLater(() -> labelCount.setText("已领资料:" + getPaper + "  已打印:" + printed + "   总人数:" + total));

    }

    private int queryForIntResult(String countString) {
        int recordsCount = 0;
        try {
            Connection conn = DataBaseUtility.getConnection();
            PreparedStatement stm = conn.prepareStatement(countString);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                recordsCount = rs.getInt(1);
            }
            conn.close();
            return recordsCount;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log(e.getMessage());
            return -1;
        }
    }

    @Override
    public void stop() {
        if (DataBaseUtility.sonicServer != null) {
            DataBaseUtility.sonicServer.shutdown();
            try {
                LogUtil.bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("application closed");
    }

    public void showWaiting() {
        waitingVbox.setVisible(true);
    }

    public void hideWaiting() {
        waitingVbox.setVisible(false);
    }

    private int getRowCount(GridPane pane) {
        int numRows = pane.getRowConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) {
            Node child = pane.getChildren().get(i);
            if (child.isManaged()) {
                Integer rowIndex = GridPane.getRowIndex(child);
                if (rowIndex != null) {
                    numRows = Math.max(numRows, rowIndex + 1);
                }
            }
        }
        return numRows;
    }

    private void printSelection() {
        psa = new PrintServiceApp();
        List<RegistrationInformation> regInfos = (List<RegistrationInformation>) tableUserInfo.getSelectionModel().getSelectedItems();
        for (RegistrationInformation info : regInfos) {
            printRegInfo(info);
            Wf.this.refreshLabelCount();
            RegistrationService.printSuccess(info.getId());
        }
    }

}
