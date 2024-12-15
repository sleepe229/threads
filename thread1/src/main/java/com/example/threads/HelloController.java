package com.example.threads;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
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

    private final List<Thread> threads = new ArrayList<>();

    @FXML
    void trySmoke(){
        Thread paper = new Thread(new Smoker("paper", circlePaperSmoker));
        Thread matches = new Thread(new Smoker("matches", circleMatchSmoker));
        Thread tobacco = new Thread(new Smoker("tobacco", circleTobaccoSmoker));
        Thread agent = new Thread(new Agent(circlePaperSmoker, circleMatchSmoker, circleTobaccoSmoker));

        threads.add(paper);
        threads.add(matches);
        threads.add(tobacco);
        threads.add(agent);

        paper.start();
        matches.start();
        tobacco.start();
        agent.start();
    }

    public void stopAllThreads() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
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
                            updateColorUI(circle, Color.GREEN);
                        }
                        case "matches" -> {
                            smokerMatchesSem.acquireUninterruptibly();
                            updateColorUI(circle, Color.GREEN);
                        }
                        case "tobacco" -> {
                            smokerTobaccoSem.acquireUninterruptibly();
                            updateColorUI(circle, Color.GREEN);
                        }
                    }

                    updateColorUI(circle, Color.RED);
                    out.println("smoker " + ingredient + " smoking");
                    agentSem.release();

                    Thread.sleep(5000);
                    updateColorUI(circle, Color.YELLOW);



                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    break;
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
//                            updateColorUI(circlePaper, Color.BLUE);
                        }
                        case 1 -> {
                            out.println("agent takes paper and matches");
                            paperOnTable = true;
                            matchesOnTable = true;
                            smokerTobaccoSem.release();
//                            updateColorUI(circleTobacco, Color.BLUE);
                        }
                        case 2 -> {
                            out.println("agent takes tobacco and paper");
                            tobaccoOnTable = true;
                            paperOnTable = true;
                            smokerMatchesSem.release();
//                            updateColorUI(circleMatches, Color.BLUE);
                        }
                    }

                    agentSem.acquireUninterruptibly();
                    Thread.sleep(100);

                }
                catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private static void updateColorUI(Circle circle, Color color) {
        Platform.runLater(() -> circle.setFill(color));
    }

}