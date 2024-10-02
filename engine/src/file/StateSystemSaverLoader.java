package file;

import exception.FileException;
import impl.EngineImpl;

import java.io.*;

public class StateSystemSaverLoader {

    public static void writeSystemToFile(EngineImpl engine, String fileName) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(fileName))) {
            out.writeObject(engine);
            out.flush();
        } catch (FileNotFoundException e) {
            throw new FileException("The specified file path " + fileName + " was not found.");
        } catch (IOException e) {
            throw new FileException("Failed to save the system state to the file: ");
        }
    }

    public static EngineImpl readSystemFromFile(String fileName){
        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(fileName))) {
            EngineImpl engineFromFile = (EngineImpl) in.readObject();
            return engineFromFile;
        }catch (FileNotFoundException e) {
            throw new FileException("The specified file path " + fileName + " was not found.");
        } catch (IOException e) {
            throw new FileException("Failed to read the system state from the file: " + fileName);
        } catch (ClassNotFoundException e) {
            throw new FileException("The required class for deserialization was not found while reading from: " + fileName);
        }
    }
}
