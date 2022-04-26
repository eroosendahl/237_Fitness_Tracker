package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commands.AppendEntryCommand;
import commands.DeleteEntryCommand;
import commands.NewEntryCommand;
import commands.NewUserCommand;
import main.CommandPrompt;
import main.User;

class AppendEntryCommandTests {
	CommandPrompt commandPrompt;
	AppendEntryCommand appendEntry;
	NewUserCommand newUser;
	NewEntryCommand newEntry;
	DeleteEntryCommand deleteEntryCommand;
	String testFileName = "testFile";

	@BeforeEach
	void setup() throws IOException {
		commandPrompt = new CommandPrompt();
		createTestFile(testFileName);
		commandPrompt.setFile(testFileName);
		appendEntry = new AppendEntryCommand(commandPrompt);
		commandPrompt.addCommand(appendEntry);
		newEntry = new NewEntryCommand(commandPrompt);
		commandPrompt.addCommand(newEntry);
		newUser = new NewUserCommand(commandPrompt);
		commandPrompt.addCommand(newUser);
		deleteEntryCommand = new DeleteEntryCommand(commandPrompt);
		commandPrompt.addCommand(deleteEntryCommand);
		
		String exampleUserName = "testUser";
		User originalUser = new User(exampleUserName, 0);
		String entry1 = "09/01/2001 run 500";
		String entry2 = "10/01/2001 run 500";
		
		newUser.execute(exampleUserName);
		commandPrompt.setCurrentUser(originalUser);
		newEntry.execute(entry1);
		newEntry.execute(entry2);
	}
	
	@Test
	void validAppendEntry() throws IOException {
		String exampleAppendEntry = "09/01/2001 walk 300";
		appendEntry.execute(exampleAppendEntry);
		
		String newEntry = "09/01/2001 run(500) walk(300)";
		boolean foundNewEntry = searchForEntry(testFileName, commandPrompt.getCurrentUser(), newEntry);
		String oldEntry = "10/01/2001 run(500)";
		boolean foundOldEntry = searchForEntry(testFileName, commandPrompt.getCurrentUser(), oldEntry);
		deleteTestFile(testFileName);
		
		assertTrue(foundNewEntry);
		assertTrue(foundOldEntry);
	}
	
	@Test
	void invalidAppendEntry() throws IOException {
		String exampleAppendEntry = "09/01/2001 walk";
		appendEntry.execute(exampleAppendEntry);
		
		String newEntry = "09/01/2001 run(500)";
		boolean foundNewEntry = searchForEntry(testFileName, commandPrompt.getCurrentUser(), newEntry);
		String oldEntry = "10/01/2001 run(500)";
		boolean foundOldEntry = searchForEntry(testFileName, commandPrompt.getCurrentUser(), oldEntry);
		deleteTestFile(testFileName);
		
		assertTrue(foundNewEntry);
		assertTrue(foundOldEntry);
	}
	
	public boolean createTestFile(String fileName) throws IOException {
		File testFile = new File(fileName);
		return testFile.createNewFile();
	}
	
	public boolean deleteTestFile(String fileName) {
		File testFile = new File(fileName);
		return testFile.delete();
	}
	
	public boolean searchForEntry(String fileName, User user ,String entry) throws IOException {
		File csvFile = new File(fileName);
		FileReader csvReader = new FileReader(csvFile);
		BufferedReader csvBufferedReader = new BufferedReader(csvReader);
		String line = null;
		boolean found = false;

		int count = 0;
		while ((line = csvBufferedReader.readLine()) != null) {
			if(user.getRow()==count) {
				String[] entries = line.split(",");
				for(int i =1; i <entries.length; i++) {
					if(entries[i].contains(entry)) {
						found = true;
						csvReader.close();
						csvBufferedReader.close();
						return found;
					}
				}
			}
			count++;
		}
		csvReader.close();
		csvBufferedReader.close();
		
		return found;
	}
}
