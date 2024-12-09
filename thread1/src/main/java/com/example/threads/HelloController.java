package com.example.threads;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Random;
import java.util.concurrent.Semaphore;

import static java.lang.System.out;

public class HelloController {
    @FXML
    private Button start_btn;
    @FXML
    private Circle circleMatchSmoker;
    @FXML
    private Circle circleTobaccoSmoker;
    @FXML
    private Circle circlePaperSmoker;

    static Semaphore smokerPaperSem = new Semaphore(0);
    static Semaphore smokerTobaccoSem = new Semaphore(0);
    static Semaphore smokerMatchesSem = new Semaphore(0);
    static Semaphore agentSem = new Semaphore(1);

    static boolean tobaccoOnTable = false;
    static boolean paperOnTable = false;
    static boolean matchesOnTable = false;

    @FXML
    void trySmoke(){
        Thread paper = new Thread(new Smoker("paper", circlePaperSmoker));
        Thread matches = new Thread(new Smoker("matches", circleMatchSmoker));
        Thread tobacco = new Thread(new Smoker("tobacco", circleTobaccoSmoker));
        Thread agent = new Thread(new Agent(circlePaperSmoker, circleMatchSmoker, circleTobaccoSmoker));

        paper.start();
        matches.start();
        tobacco.start();
        agent.start();
    }

    private static class Smoker implements Runnable{
        private String ingredient;
        private Circle circle;

        public Smoker(String ingredient, Circle circle) {
            this.ingredient = ingredient;
            this.circle = circle;
        }

        @Override
        public void run() {
            while(true) {
                try{
                    switch (ingredient) {
                        case "paper" -> {
                            smokerPaperSem.acquireUninterruptibly();
                            updateColorUI(circle, Color.YELLOW);
                        }
                        case "matches" -> {
                            smokerMatchesSem.acquireUninterruptibly();
                            updateColorUI(circle, Color.YELLOW);
                        }
                        case "tobacco" -> {
                            smokerTobaccoSem.acquireUninterruptibly();
                            updateColorUI(circle, Color.YELLOW);
                        }
                    }
                    if ((ingredient.equals("paper") && tobaccoOnTable && matchesOnTable) ||
                            (ingredient.equals("matches") && tobaccoOnTable && paperOnTable) ||
                            (ingredient.equals("tobacco") && paperOnTable && matchesOnTable)) {

                        out.println("smoker " + ingredient + " make cigarette");
                        Thread.sleep(10000);

                        if (ingredient.equals("paper")) {
                            paperOnTable = false;
                        } else if (ingredient.equals("matches")) {
                            matchesOnTable = false;
                        } else if (ingredient.equals("tobacco")) {
                            tobaccoOnTable = false;
                        }

                        updateColorUI(circle, Color.RED);
                        out.println("smoker " + ingredient + " smoking");
                        agentSem.release();

                        Thread.sleep(10000);
                        updateColorUI(circle, Color.GREEN);
                    }


                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    private static class Agent implements Runnable{
        private Circle circlePaper;
        private Circle circleMatches;
        private Circle circleTobacco;

        public Agent(Circle circlePaper, Circle circleMatches, Circle circleTobacco) {
            this.circlePaper = circlePaper;
            this.circleMatches = circleMatches;
            this.circleTobacco = circleTobacco;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    var random = new Random().nextInt(3);

                    switch (random) {
                        case 0 -> {
                            out.println("agent takes tobacco and matches");
                            tobaccoOnTable = true;
                            matchesOnTable = true;
                            smokerPaperSem.release();
                            updateColorUI(circlePaper, Color.BLUE);
                        }
                        case 1 -> {
                            out.println("agent takes paper and matches");
                            paperOnTable = true;
                            matchesOnTable = true;
                            smokerTobaccoSem.release();
                            updateColorUI(circleTobacco, Color.BLUE);
                        }
                        case 2 -> {
                            out.println("agent takes tobacco and paper");
                            tobaccoOnTable = true;
                            paperOnTable = true;
                            smokerMatchesSem.release();
                            updateColorUI(circleMatches, Color.BLUE);
                        }
                    }

                    agentSem.acquireUninterruptibly();
                    Thread.sleep(10000);

                }
                catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private static void updateColorUI(Circle circle, Color color) {
        Platform.runLater(() -> circle.setFill(color));
    }

}