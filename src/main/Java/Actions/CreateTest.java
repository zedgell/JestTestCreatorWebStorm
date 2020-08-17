package Actions;

import Notifactions.CreateTestError;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import popUps.TestLocation;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CreateTest extends AnAction {
    public static VirtualFile projectLocation;
    public static File testFolder;
    public static VirtualFile file;
    public static String pathToFile;
    public static List<String> methods;
    @Override
    public void update(AnActionEvent e) {
        Project p  = e.getData(PlatformDataKeys.PROJECT);
        assert p != null;
        projectLocation = ModuleRootManager.getInstance(ModuleManager.getInstance(p).getModules()[0]).getContentRoots()[0];
        file = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        assert file != null;
        if(!getFileExtension(file).equals("vue")){
            CreateTestError.notify("Creating Test can only be ran on vue files not " + getFileExtension(file)
            + " files");
        } else {
            try {
                methods = getMethods(file);
                TestLocation.createWindow();
            } catch (IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("ran");
    }
    private static String getFileExtension(VirtualFile file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    public static List<String> getMethods(VirtualFile file) throws IOException {
        List<String> methods = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
        String st;
        Boolean script = false;
        while ((st = br.readLine()) != null){
            if(st.contains("<script>")){
                script = true;
            }
            if(st.contains("</script>")){
                script = false;
            }
            if(st.contains("(") && !st.contains(".") && script && !st.contains("let") && !st.contains("const") &&
                    !st.contains("var")){
                if(st.contains("function")){
                    System.out.println(st);
                    st = st.replace("function", "");
                }
                String method = st.split("[(]")[0].replace(" ", "");
                if(!method.equals("data")){
                    methods.add(method);
                    System.out.println(method);
                }
            }
        }
        return methods;
    }
    public static void getRelativeFilePath(){
        if(testFolder != null){
            Path base = Paths.get(testFolder.getPath());
            Path pathAbsolute = Paths.get(file.getPath());
            Path pathRelative = base.relativize(pathAbsolute);
            pathToFile = pathRelative.toString();
        }
    }

    public static void writeTestFile() throws IOException {
        String name = file.getName().split("[.]")[0] + ".spec.js";
        System.out.println(testFolder.getPath()+ "/" + name);
        File testFile = new File(testFolder.getPath()+ "/" + name);
        testFile.createNewFile();
        PrintStream fileStream = new PrintStream(testFile);
        fileStream.println("import " + file.getName().split("[.]")[0] + " from \"" +
                pathToFile.replaceAll("\\\\", "/") + "\"");
        fileStream.println("import {localVue} from '../../tools/QuasarComponents';");
        fileStream.println("import {createMockJson} from '../../tools/mockGenerator';");
        fileStream.println("import {shallowMount} from '@vue/test-utils';");
        fileStream.println("");
        fileStream.println("describe('test " + file.getName().split("[.]")[0] + "', () => {");
        fileStream.println("  let wrapper");
        fileStream.println("  beforeEach(() => {");
        fileStream.println("    wrapper = shallowMount(" + file.getName().split("[.]")[0] + ", {");
        fileStream.println("      localVue,");
        fileStream.println("   })");
        fileStream.println(" })");
        for(String methodName : methods){
            fileStream.println("  test('" + methodName + "', () => {");
            fileStream.println("    expect(true).toEqual(true)");
            fileStream.println("  })");
        }
        fileStream.println("})");
    }
}
