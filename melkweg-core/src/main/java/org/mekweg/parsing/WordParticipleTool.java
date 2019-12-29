package org.mekweg.parsing;

import org.mekweg.parsing.mark.KeyworkItem;
import org.mekweg.parsing.mark.SymbolItem;
import org.mekweg.parsing.mark.Word;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WordParticipleTool {
    public static List<Word> createWordParticipleList(String path) throws Exception {
        InputStream fileStream = WordParticipleTool.class.getClassLoader().getResourceAsStream(path);
        if (fileStream == null) {
            throw new Exception();
        }

        BufferedReader fileBufferReader = new BufferedReader(new InputStreamReader(fileStream));
        String itemFile = "";
        int line = 0;
        List<Word> wordList = new ArrayList<>();
        while ((itemFile = fileBufferReader.readLine()) != null) {
            StringBuilder keyWord = new StringBuilder();
            for (int a = 0; a < itemFile.length(); a++) {
                char i = itemFile.charAt(a);
                if (i == ' ' || i == '\n' || i == '\t') {
                    continue;
                }
                if (KeyworkItem.isNameStartKey(i)) {
                    int b = a + 1;
                    for (; b < itemFile.length(); b++) {
                        if (KeyworkItem.isNameEndKey(itemFile.charAt(b))) {
                            break;
                        }
                    }
                    wordList.add(Word.create(KeyworkItem.getKeyWorkItem("("), line, a));
                    wordList.add(new SymbolItem(itemFile.substring(a + 1, b), line, a + 1));
                    if (b != itemFile.length()) {
                        wordList.add(Word.create(KeyworkItem.getKeyWorkItem(")"), line, b));
                    }
                    keyWord = new StringBuilder();
                    a = b;
                } else {
                    keyWord.append(i);
                    Word word = KeyworkItem.getKeyWorkItem(keyWord.toString());
                    if (word != null) {
                        Word newWord = Word.create(word, line, a + 1 - word.getValue().length());
                        wordList.add(newWord);
                        keyWord = new StringBuilder();
                    }
                }
            }
            line++;
        }
        return wordList;
    }
}