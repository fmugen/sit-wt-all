package org.sitoolkit.wt.gui.pres;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.sitoolkit.wt.gui.app.script.GetCaseNoStdoutListener;
import org.sitoolkit.wt.gui.app.script.ScriptService;
import org.sitoolkit.wt.gui.app.test.TestService;
import org.sitoolkit.wt.gui.domain.script.LastCaseId;
import org.sitoolkit.wt.gui.infra.fx.FileSystemWatchService;
import org.sitoolkit.wt.gui.infra.fx.FileTreeItem;
import org.sitoolkit.wt.gui.infra.fx.FileWrapper;
import org.sitoolkit.wt.gui.infra.fx.FxContext;
import org.sitoolkit.wt.gui.infra.process.ProcessExitCallback;
import org.sitoolkit.wt.gui.infra.util.StrUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.GridPane;

public class FileTreeController implements Initializable {

    @FXML
    private TreeView<FileWrapper> fileTree;

    private Mode mode = Mode.NORMAL;

    TestService testService = new TestService();

    FileSystemWatchService fileSystemWatchService = new FileSystemWatchService();

    TestToolbarController testToolbarController;

    ScriptService scriptService = new ScriptService();

    private Map<String, LastCaseId> caseIdStore = Collections.synchronizedMap(new HashMap<>());

    public FileTreeController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileTree.setEditable(true);
        fileTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        if (mode == Mode.CHECKBOX) {
            fileTree.setCellFactory(CheckBoxTreeCell.forTreeView());
        }
        fileTree.setShowRoot(false);

        fileSystemWatchService.init();
    }

    public void destroy() {
        fileSystemWatchService.destroy();
    }

    public void setFileTreeRoot(File baseDir) {

        TreeItem<FileWrapper> root = new TreeItem<>();
        root.setValue(new FileWrapper(baseDir));

        FileTreeItem pagescriptItem = new FileTreeItem(newDir(baseDir, "pagescript"));
        pagescriptItem.buildChildren();
        root.getChildren().add(pagescriptItem);
        FileTreeItem testscriptItem = new FileTreeItem(newDir(baseDir, "testscript"));
        testscriptItem.buildChildren();
        root.getChildren().add(testscriptItem);
        FileTreeItem evidenceItem = new FileTreeItem(newDir(baseDir, "evidence"));
        evidenceItem.buildChildren();
        root.getChildren().add(evidenceItem);

        fileTree.setRoot(root);

        fileSystemWatchService.register(pagescriptItem);
        fileSystemWatchService.register(testscriptItem);
        fileSystemWatchService.register(evidenceItem);
    }

    private File newDir(File baseDir, String dir) {
        File newDir = new File(baseDir, dir);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        return newDir;
    }

    public List<File> getSelectedFiles() {
        List<File> selectedFiles = new ArrayList<>();

        if (mode == Mode.NORMAL) {
            return getFilesRecursively(fileTree.getSelectionModel().getSelectedItems());
        }

        for (TreeItem<?> item : fileTree.getRoot().getChildren()) {
            if (item instanceof FileTreeItem) {
                FileTreeItem casted = (FileTreeItem) item;
                if (casted.isSelectable()) {
                    selectedFiles.addAll(casted.getSelectedFiles());
                }
            }
        }

        return selectedFiles;
    }

    public List<File> getFilesRecursively(List<?> fileTree) {
        List<File> allFiles = new ArrayList<>();

        for (Object item : fileTree) {
            if (item instanceof FileTreeItem) {
                FileTreeItem casted = (FileTreeItem) item;
                File target = casted.getValue().getFile();

                if (target.isDirectory()) {
                    allFiles.addAll(getFilesRecursively(casted.getChildren()));
                } else {
                    allFiles.add(target);
                }
            }
        }

        return allFiles;
    }

    @FXML
    public void open() {
        operateSelectedItem(selectedItem -> {
            FxContext.openFile(selectedItem.getValue().getFile());
        });
    }

    @FXML
    public void newFolder() {
        operateSelectedDir(selectedItem -> {

            TextInputDialog dialog = new TextInputDialog("新しいフォルダー");
            dialog.setTitle("新規フォルダー");
            dialog.setHeaderText("フォルダーの名前を入力してください。");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                File newDir = new File(selectedItem.getValue().getFile(), name);

                if (newDir.exists()) {
                    return;
                }

                newDir.mkdir();
            });

        });
    }

    @FXML
    public void newScript() {
        operateSelectedDir(selectedItem -> {
            TextInputDialog dialog = new TextInputDialog("NewTestScript.xlsx");
            dialog.setTitle("新規テストスクリプト");
            dialog.setHeaderText("テストスクリプトの名前を入力してください。");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                name = name.endsWith(".xlsx") ? name : name + ".xlsx";
                File newTestScript = new File(selectedItem.getValue().getFile(), name);

                if (newTestScript.exists()) {
                    // TODO ファイル名重複
                    return;
                }

                testService.createNewScript(fileTree.getRoot().getValue().getFile(), newTestScript);
            });

        });
    }

    @FXML
    public void rename() {
        operateSelectedItem(selectedItem -> {
            File currentFile = selectedItem.getValue().getFile();

            TextInputDialog dialog = new TextInputDialog(currentFile.getName());
            dialog.setTitle("名称変更");
            dialog.setHeaderText("新しい名前を入力してください。");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                File newFile = new File(currentFile.getParent(), name);

                if (newFile.exists()) {
                    return;
                }
                currentFile.renameTo(newFile);
            });
        });
    }

    @FXML
    public void delete() {
        TreeItem<FileWrapper> selectedItem = fileTree.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            return;
        }

        selectedItem.getValue().getFile().delete();
        selectedItem.getParent().getChildren().remove(selectedItem);
    }

    @FXML
    public void allExecute() {
        testToolbarController.run();
    }

    @FXML
    public void selectExecute() {
        operateSelectedItem(selectedItem -> {
            File currentFile = selectedItem.getValue().getFile();

            String filePath = currentFile.getAbsolutePath();
            if (currentFile.isFile() && (currentFile.getName().endsWith(".xlsx") || (currentFile.getName().endsWith(".csv")))) {
                long lastModify = currentFile.lastModified();
                if (caseIdStore.get(filePath) == null || caseIdStore.get(filePath).getLastModify() != lastModify) {
                    GetCaseNoStdoutListener getCaseNoListener = new GetCaseNoStdoutListener();
                    ProcessExitCallback callback = exitCode -> {
                        Platform.runLater(() -> showSelectDialog(currentFile, getCaseNoListener.getCaseNoList()));
                        List<String> caseIdList = getCaseNoListener.getCaseNoList();
                        caseIdStore.put(filePath, new LastCaseId(filePath, caseIdList, lastModify));
                    };
                    scriptService.getCaseNo(selectedItem.getValue().getFile().getAbsolutePath(), getCaseNoListener, callback);
                } else {
                    showSelectDialog(currentFile, caseIdStore.get(filePath).getCaseIdList());
                }

            } else {
                showAlert("[ケースを選択して実行]はxlsx、csvのみが対象です。");
            }

        });
    }

    private void operateSelectedItem(Operation operation) {
        TreeItem<FileWrapper> selectedItem = fileTree.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            return;
        }

        operation.operate(selectedItem);
    }

    private void operateSelectedDir(Operation operation) {
        operateSelectedItem(selectedItem -> {
            File dir = selectedItem.getValue().getFile();
            if (dir.isFile()) {
                dir = dir.getParentFile();
                selectedItem = selectedItem.getParent();
            }
            operation.operate(selectedItem);
        });
    }

    public void setTestToolbarController(TestToolbarController testToolbarController) {
        this.testToolbarController = testToolbarController;
    }

    public void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("");
        alert.setContentText("");
        alert.setHeaderText(message);
        alert.show();
    }

    public void showSelectDialog(File currentFile, List<String> caseIdList) {
        if (caseIdList.isEmpty()) {
            showAlert("実行可能な試験ケースが存在しません。");
            return;
        }

        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("ケースを指定して実行");
        dialog.setHeaderText("実行するテストケースを選択してください。");

        ButtonType execButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(execButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label(currentFile.getName()), 0, 0);
        int cnt = 1;
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (String caseId : caseIdList) {
            CheckBox checkBox = new CheckBox(caseId);
            checkBoxes.add(checkBox);
            grid.add(checkBox, 1, cnt);
            cnt++;
        }
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == execButtonType) {
                List<String> selectedCaseId = new ArrayList<>();
                for (CheckBox checkBox : checkBoxes) {
                    if (checkBox.isSelected()) {
                        selectedCaseId.add(currentFile.getAbsolutePath() + "#" + checkBox.getText());
                    }
                }
                return selectedCaseId;
            }
            return null;
        });

        Optional<List<String>> result = dialog.showAndWait();
        result.ifPresent(selectedCaseId -> {
            if (selectedCaseId == null || selectedCaseId.isEmpty()) {
                showAlert("ケースが選択されていません。");
            } else {
                testToolbarController.runNominateCase(StrUtils.join(selectedCaseId));
            }
        });
    }

    @FunctionalInterface
    interface Operation {
        void operate(TreeItem<FileWrapper> selectedItem);
    }

    enum Mode {
        NORMAL, CHECKBOX
    }

}
