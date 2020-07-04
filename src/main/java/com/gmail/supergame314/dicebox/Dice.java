package com.gmail.supergame314.dicebox;

import java.util.Random;

public class Dice {

    Random rnd;
    int max;

    public Dice(int max){
        this.max = max;
        rnd = new Random();
    }

    public Dice(String max){
        try {
            this.max = Integer.parseInt(max);
        }catch (NumberFormatException ex){
            this.max = 6;
        }
        rnd = new Random();
    }

    public int goThrow(){
        return rnd.nextInt(max)+1;
    }

}