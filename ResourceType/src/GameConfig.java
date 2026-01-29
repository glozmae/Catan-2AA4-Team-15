import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class GameConfig {
    Path filePath = Paths.get(filename);
    int turns;
    public GameConfig(String filename){
        try{
            String content = Files.readString(filePath);
            int i = content.indexOf("turns: ");

            if (i == -1){
                System.out.println("Missing the word 'turns' in the file");
                return;
            }


            String after = content.substring(i + 6).trim();
            String number = after;
            turns = Integer.parseInt(number);

        }catch(IOException e){
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public int getTurns(){
        return turns;
    }
}