/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ak.da.capscreen;

import java.io.File;

/**
 *
 * @author Muhi
 */
public class History {
    private File lastOne;
    private File lastTwo;
    private File lastThree;
    private File lastFour;

    public History(File lastOne, File lastTwo, File lastThree, File lastFour) {
        this.lastOne = lastOne;
        this.lastTwo = lastTwo;
        this.lastThree = lastThree;
        this.lastFour = lastFour;
    }

    public History() {
        
    }
    
    public void setLastOne(File lastOne) {
        this.lastOne = lastOne;
    }

    public void setLastTwo(File lastTwo) {
        this.lastTwo = lastTwo;
    }

    public void setLastThree(File lastThree) {
        this.lastThree = lastThree;
    }

    public void setLastFour(File lastFour) {
        this.lastFour = lastFour;
    }
    
    public File[] getFiles(){
        return new File[]{lastFour, lastThree, lastTwo, lastOne};
    }

    public File getLastOne() {
        return lastOne;
    }

    public File getLastTwo() {
        return lastTwo;
    }

    public File getLastThree() {
        return lastThree;
    }

    public File getLastFour() {
        return lastFour;
    }
    
    
}
