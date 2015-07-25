package ru.iostd.safebox.wizard;

import java.util.Stack;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

abstract public class Wizard extends StackPane {

        private static final int UNDEFINED = -1;
        private final ObservableList<WizardPage> pages = FXCollections.observableArrayList();
        private final Stack<Integer> history = new Stack<>();
        private int curPageIdx = UNDEFINED;

        public Wizard(WizardData data, Stage owner, WizardPage... nodes) {
            super();
            for(WizardPage node : nodes) {
                node.setData(data);
                node.setOwner(owner);
            }
            pages.addAll(nodes);
            navTo(0);
            setStyle("-fx-padding: 10; ");
        }

        public void nextPage() {
            if (hasNextPage()) {
                navTo(curPageIdx + 1);
            }
        }

        public void priorPage() {
            if (hasPriorPage()) {
                navTo(history.pop(), false);
            }
        }

        public boolean hasNextPage() {
            return (curPageIdx < pages.size() - 1);
        }

        public boolean hasPriorPage() {
            return !history.isEmpty();
        }

        public void navTo(int nextPageIdx, boolean pushHistory) {
            if (nextPageIdx < 0 || nextPageIdx >= pages.size()) {
                return;
            }
            if (curPageIdx != UNDEFINED) {
                if (pushHistory) {
                    history.push(curPageIdx);
                }
            }

            WizardPage nextPage = pages.get(nextPageIdx);
            curPageIdx = nextPageIdx;
            getChildren().clear();
            getChildren().add(nextPage);
            nextPage.manageButtons();
            nextPage.call();
        }

        public void navTo(int nextPageIdx) {
            navTo(nextPageIdx, true);
        }

        public void navTo(String id) {
            Node page = lookup("#" + id);
            if (page != null) {
                int nextPageIdx = pages.indexOf(page);
                if (nextPageIdx != UNDEFINED) {
                    navTo(nextPageIdx);
                }
            }
        }

        abstract public void finish();

        abstract public void cancel();
    }