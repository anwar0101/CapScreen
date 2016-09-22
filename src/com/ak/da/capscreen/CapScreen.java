/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ak.da.capscreen;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author user
 */
public class CapScreen extends Application {
    public static Stage priStage;
    @Override
    public void start(Stage primaryStage) {
        CapScreen.priStage = primaryStage;
        //root group
        Group root = new Group();
        //scene
        Scene scene = new Scene(root, 100, 100);
        
//        javafx.scene.shape.Rectangle rect = 
//                new javafx.scene.shape.Rectangle(100, 100);
        
        Button btnShot = new Button("Shot");
        btnShot.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    captureScreen();
                } catch (Exception ex) {
                    //
                }
            }
        });
        
        root.getChildren().add(btnShot);
        
        primaryStage.setTitle("CapScreen");
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }

    @Override
    public void stop() throws Exception {
        super.stop(); //To change body of generated methods, choose Tools | Templates.
        System.exit(0);
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
