/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ak.da.capscreen;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.media.AudioClip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import jhook.Keyboard;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

/**
 *
 * @author user
 */
public class CapScreen extends Application {
    public static Stage priStage;
    private double initX;
    private double initY;
    private TrayIcon trayIcon;
    private String lastDir;
    private boolean isRandom = false;
    private boolean isCombine = false;
    private History history = new History();
    private HBox historyPanel;
    private HBox allContent;
    
    private static final AudioClip ALERT_AUDIOCLIP = new AudioClip(CapScreen.class.getResource("snap.wav").toString());
    private ImageView filesIcon;
    
    private HBox historyGenerate(){
        HBox hbox = new HBox();
        for(File f: history.getFiles()){
            if(filesIcon == null){
                filesIcon = new ImageView(
                new javafx.scene.image.Image(CapScreen.class.getResourceAsStream("image.png"), 20, 20, false, false));
            }
            
            if(f != null && filesIcon != null){
                Label label = new Label("",filesIcon);
                label.setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        /* drag was detected, start a drag-and-drop gesture*/
                        /* allow any transfer mode */
                        Dragboard db = label.startDragAndDrop(TransferMode.ANY);

                        /* Put a string on a dragboard */
                        ClipboardContent content = new ClipboardContent();
                        File file = history.getLastOne();
                        List<File> files = new ArrayList<>();
                        files.add(file);
                        content.putFiles(files);
                        db.setContent(content);

                        event.consume();
                    }
                });
                hbox.getChildren().add(label);
            }
        }
        return hbox;
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        
        createTrayIcon(primaryStage);
        Platform.setImplicitExit(false);
        
        CapScreen.priStage = primaryStage;
        //root group
        Group root = new Group();
        //scene
        Scene scene = new Scene(root, 200, 54);
        
        Button btnShot = new Button("",
                new ImageView(new javafx.scene.image.Image(CapScreen.class.getResourceAsStream("icon128.png"), 15, 15, false, false)));
        btnShot.setOnAction((ActionEvent event) -> {
            try {
                CapScreen.ALERT_AUDIOCLIP.play();
                captureScreen();
            } catch (Exception ex) {
                //
            }
        });
        
        BorderPane borderPane = new BorderPane();
//        borderPane.setStyle("-fx-background-color: green;");
        
        Label title = new Label("CapScreen");
        ImageView imgView = new ImageView(
                new javafx.scene.image.Image(CapScreen.class.getResourceAsStream("capscreen.png"), 15, 15, false, false));
        
        title.setGraphic(imgView);
        title.setGraphicTextGap(2);
        HBox hbox = new HBox(title);
        ToolBar toolBar = new ToolBar();
        
        int height = 25;
        toolBar.setPrefHeight(height);
        toolBar.setMinHeight(height);
        toolBar.setMaxHeight(height);
        toolBar.setPrefWidth(200);
        
        toolBar.getItems().add(hbox);
        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        toolBar.getItems().add(pane);
        toolBar.getItems().add(new WindowButtons());

        borderPane.setTop(toolBar);
        
        root.getChildren().add(borderPane);
        
        
        //when mouse button is pressed, save the initial position of screen
        toolBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                initX = me.getScreenX() - primaryStage.getX();
                initY = me.getScreenY() - primaryStage.getY();
            }
        });

        //when screen is dragged, translate it accordingly
        toolBar.setOnMouseDragged((MouseEvent me) -> {
            primaryStage.setX(me.getScreenX() - initX);
            primaryStage.setY(me.getScreenY() - initY);
        });
        
        
        historyPanel = historyGenerate();
        
        allContent = new HBox(btnShot, historyPanel);
        allContent.setMaxHeight(25);
        allContent.setPrefHeight(25);
        allContent.setPrefWidth(200);
        allContent.setPadding(new Insets(2, 10, 2, 10));
        borderPane.setBottom(allContent);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        Keyboard kb = new Keyboard();
        kb.addListener((boolean keyDown, int vk) -> {
            if(keyDown){
                
                if(vk == 44){
                    if(isCombine){
                        isRandom = true;
                        try {
                            Platform.runLater(() -> {
                                try {
                                    CapScreen.ALERT_AUDIOCLIP.play();
                                    captureScreen();
                                } catch (Exception ex) {
                                    //error log
                                }
                            });
                        } catch (Exception ex) {
                            //error
                        }
                    } else {
                        isRandom = false;
                        try {
                            Platform.runLater(() -> {
                                try {
                                    CapScreen.ALERT_AUDIOCLIP.play();
                                    captureScreen();
                                } catch (Exception ex) {
                                    //error log
                                }
                            });
                        } catch (Exception ex) {
                            //error
                        }
                    }
                }
                
                if(vk == 16 || vk == 160){
                    isCombine = true;
                } else {
                    isCombine = false;
                }
                
                System.out.println("keypress: " + vk);
                
            }
            
        });
        
    }
    
    
    /**
     * capture function
     * @throws java.lang.Exception
    */
    
    public void captureScreen() throws Exception {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice screen = env.getDefaultScreenDevice();
        Robot robot = new Robot(screen);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        BufferedImage img = robot.createScreenCapture(new Rectangle(0, 0, d.width, d.height));
        File file;
        if(isRandom){
            file = saveWithRandom();
        } else {
            file = saveImage();
        }
        if(file != null){
            ImageIO.write(img, "png", file);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    allContent.getChildren().remove(historyPanel);
                    history.setLastOne(file);
                    historyPanel = historyGenerate();
                    allContent.getChildren().add(historyPanel);
                }
            });
        } else {
            System.out.println("file not saved.");
        }
    }
    
    /**
     * save dialog show
     */
    private File saveImage(){
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName("capscreen.png");
        chooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
        chooser.setTitle("Save File");
        if(lastDir != null){
            chooser.setInitialDirectory(new File(lastDir));
        } else {
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        File file = chooser.showSaveDialog(priStage);
        lastDir = file.getParent();
        return file;
    }
    
    private File saveWithRandom(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        Date date = Calendar.getInstance().getTime();
        String randomName = sdf.format(date);
        String fileName = System.getProperty("user.home") 
                + "\\CapScreen\\" + randomName + ".png";
        File file = new File(fileName);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
       return file;
        
    }
    
    
    public void createTrayIcon(final Stage stage) {
        if (SystemTray.isSupported()) {
            // get the SystemTray instance
            SystemTray tray = SystemTray.getSystemTray();
            // load an image
            java.awt.Image image = null;
            image =
                    new ImageIcon(CapScreen.class.getResource("capscreen.png"))
                            .getImage().getScaledInstance(15, 15, Image.SCALE_DEFAULT);
             
            stage.setOnCloseRequest((WindowEvent t) -> {
                hide(stage);
            });
            // create a action listener to listen for default action executed on the tray icon
            final ActionListener closeListener = (java.awt.event.ActionEvent e) -> {
                System.exit(0);
            };

            ActionListener showListener = new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.show();
                        }
                    });
                }
            };
            // create a popup menu
            PopupMenu popup = new PopupMenu();

            MenuItem showItem = new MenuItem("Show");
            showItem.addActionListener(showListener);
            popup.add(showItem);

            MenuItem closeItem = new MenuItem("Exit");
            closeItem.addActionListener(closeListener);
            popup.add(closeItem);
            /// ... add other items
            // construct a TrayIcon
            trayIcon = new TrayIcon(image, "CapScreen", popup);
            // set the TrayIcon properties
            trayIcon.addActionListener(showListener);
            // ...
            // add the tray image
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e);
            }
            // ...
        }
    }
    
    public void showProgramIsMinimizedMsg() {
        TrayNotification tray =
                new TrayNotification("CapScreen", "CapScreen minimized in System Tray.", NotificationType.SUCCESS);
        tray.setAnimationType(AnimationType.POPUP);
        tray.showAndDismiss(Duration.seconds(5));
        
        final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.minimized");

        if (runnable != null) {
            runnable.run();
        }
        
    }
    
    private void hide(final Stage stage) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (SystemTray.isSupported()) {
                    stage.hide();
                    showProgramIsMinimizedMsg();
                } else {
                    System.exit(0);
                }
            }
        });
    }
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * WindowButtons for windows border button
     */
    
    class WindowButtons extends HBox {
        
        public WindowButtons(){
            Button closeBtn = new Button("X");
            
            closeBtn.setOnAction((ActionEvent event) -> {
                CapScreen.priStage.close();
                showProgramIsMinimizedMsg();
            });
            
            Button btnMinimize = new Button("-");
            
            
            btnMinimize.setOnAction((ActionEvent event) -> {
                CapScreen.priStage.setIconified(true);
            });
            
            
            this.getChildren().addAll(btnMinimize, closeBtn);
            this.setSpacing(5);
        }
        
    }
    
}
