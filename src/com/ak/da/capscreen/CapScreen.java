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
import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;
import jhook.Keyboard;

/**
 *
 * @author user
 */
public class CapScreen extends Application {
    public static Stage priStage;
    private double initX;
    private double initY;
    
    private boolean firstTime;
    private TrayIcon trayIcon;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        
        createTrayIcon(primaryStage);
        firstTime = true;
        Platform.setImplicitExit(false);
        
        
        CapScreen.priStage = primaryStage;
        //root group
        Group root = new Group();
        //scene
        Scene scene = new Scene(root, 200, 50);
        
        Button btnShot = new Button("Shot");
        btnShot.setOnAction((ActionEvent event) -> {
            try {
                captureScreen();
            } catch (Exception ex) {
                //
            }
        });
        
        BorderPane borderPane = new BorderPane();
        //borderPane.setStyle("-fx-background-color: green;");

        ToolBar toolBar = new ToolBar();

        int height = 25;
        toolBar.setPrefHeight(height);
        toolBar.setMinHeight(height);
        toolBar.setMaxHeight(height);
        toolBar.setPrefWidth(200);
        toolBar.getItems().add(new WindowButtons());

        borderPane.setTop(toolBar);
        
        root.getChildren().add(borderPane);
        
        
        //when mouse button is pressed, save the initial position of screen
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                initX = me.getScreenX() - primaryStage.getX();
                initY = me.getScreenY() - primaryStage.getY();
            }
        });

        //when screen is dragged, translate it accordingly
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                primaryStage.setX(me.getScreenX() - initX);
                primaryStage.setY(me.getScreenY() - initY);
            }
        });
        
        //when mouse button is pressed, save the initial position of screen
        toolBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                initX = me.getScreenX() - primaryStage.getX();
                initY = me.getScreenY() - primaryStage.getY();
            }
        });

        //when screen is dragged, translate it accordingly
        toolBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                primaryStage.setX(me.getScreenX() - initX);
                primaryStage.setY(me.getScreenY() - initY);
            }
        });
        
        
        HBox allContent = new HBox(btnShot);
        allContent.setMaxHeight(25);
        allContent.setPrefHeight(25);
        allContent.setPrefWidth(200);
        
        borderPane.setBottom(allContent);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        Keyboard kb = new Keyboard();
        kb.addListener((boolean keyDown, int vk) -> {
            if(keyDown){
                if(vk == 44){
                    try {
                        Platform.runLater(() -> {
                            try {
                                captureScreen();
                                System.out.println("Saved.");
                            } catch (Exception ex) {
                                //error log
                            }
                        });
                    } catch (Exception ex) {
                        //error
                    }
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
        File file = saveImage();
        
        if(file != null){
            ImageIO.write(img, "png", file);
        } else {
            System.out.println("file not saved.");
        }
    }
    
    /**
     * save dialog show
     */
    private File saveImage(){
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName("shot");
        chooser.setTitle("Save File");
        
        return chooser.showSaveDialog(priStage);
    }
    
    
    public void createTrayIcon(final Stage stage) {
        if (SystemTray.isSupported()) {
            // get the SystemTray instance
            SystemTray tray = SystemTray.getSystemTray();
            // load an image
            java.awt.Image image = null;
            try {
                URL url = new URL("http://www.digitalphotoartistry.com/rose1.jpg");
                image = ImageIO.read(url);
            } catch (IOException ex) {
                System.out.println(ex);
            }


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
        
        trayIcon.displayMessage("CapScreen is minimized.",
                    "Some other message.",
                    TrayIcon.MessageType.INFO);
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
            });
            
            Button btnMinimize = new Button("-");
            
            
            btnMinimize.setOnAction((ActionEvent event) -> {
                CapScreen.priStage.setIconified(true);
            });
            
            Text title = new Text("CapScreen");
            
            this.getChildren().addAll(title, btnMinimize, closeBtn);
            this.setSpacing(5);
        }
        
    }
    
}
