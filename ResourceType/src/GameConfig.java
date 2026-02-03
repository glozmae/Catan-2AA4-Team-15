import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class GameConfig {
    Path filePath;
    int turns;

    public GameConfig(String filename){
        try{
            filePath = Paths.get(filename);

            String content= Files.readString(filePath);
            int i = content.indexOf("turns: ");
            if (i == -1){
                System.out.println("Missing the word 'turns' in the file");
                return;
            }

            String after = content.substring(i + 7).trim();

            //If there are other lines, just take the first line
            int newline = after.indexOf('\n');
            if(newline != -1){
                after = after.substring(0, newline).trim();
            }
            turns = Integer.parseInt(after);

            if (turns < 1){
                turns = 1;
            }
            if (turns > 8192){
                turns = 8192;
            }

        }catch(IOException e){
            System.out.println("Error reading file: " + e.getMessage());
        }catch(NumberFormatException e){
            System.out.println("Invalid turns value in the file");
        }
    }

    public int getTurns(){
        return turns;
    }
}