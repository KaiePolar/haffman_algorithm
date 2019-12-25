package com.company;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class Encoder {

    private String initialString;
    private TreeSet<Character> chars = new TreeSet<>();
    private ArrayList<CustomChar> queue = new ArrayList<>();
    private Map<Character, String> codedValues = new HashMap<>();
    private String codedBytesString;

    void startCompressing(String string) {
        initialString = string.substring(1, string.length() - 1);
        System.out.println(initialString);
        addChars();
    }

    private void addChars() {
        for (char x : initialString.toCharArray()) {
            chars.add(x);
        }
        countChars();
    }

    private void countChars() {
        for (char y : chars) {
            int count = 0;
            for (char z : initialString.toCharArray()) {
                if (y == z) {
                    count++;
                }
            }
            queue.add(0, new CustomChar(y, count));
        }
        sortQueue();
        System.out.println("Приоритет символов - " + queue);
        collectNodes();
    }

    private void collectNodes() {
        while (!isReady()) {
            CustomChar left = queue.get(0);
            queue.remove(0);
            CustomChar right = queue.get(0);
            queue.remove(0);
            CustomChar newChar = new CustomChar('%', left.getPriority() + right.getPriority());
            if (left.getPriority() < right.getPriority()) {
                newChar.setLeftChild(left);
                newChar.setRightChild(right);
            } else if (left.getPriority() > right.getPriority()) {
                newChar.setLeftChild(right);
                newChar.setRightChild(left);
            } else {
                newChar.setLeftChild(left);
                newChar.setRightChild(right);
            }
            queue.add(0, newChar);
            sortQueue();
        }
        setCodes(queue.get(0));
        codedValues.remove('%');
        printCodes();
    }

    private void sortQueue() {
        queue.sort((o1, o2) -> {
            if (o1.getPriority() > o2.getPriority()) {
                return 1;
            }
            if (o1.getPriority() < o2.getPriority()) {
                return -1;
            }
            return 0;
        });
    }

    private boolean isReady() {
        return queue.size() < 2;
    }

    private void setCodes(CustomChar customChar) {
        if (customChar.getLeftChild() != null) {
            customChar.getLeftChild().setCode(customChar.getCode() + "0");
            codedValues.put(customChar.getLeftChild().getCharacter(), customChar.getLeftChild().getCode());
            setCodes(customChar.getLeftChild());
        }
        if (customChar.getRightChild() != null) {
            customChar.getRightChild().setCode(customChar.getCode() + "1");
            codedValues.put(customChar.getRightChild().getCharacter(), customChar.getRightChild().getCode());
            setCodes(customChar.getRightChild());
        }
    }

    private void printCodes() {
        System.out.println("Коды символов:");
        for (char x : chars) {
            System.out.println("'" + x + "'" + " = " + codedValues.get(x) + ";");
        }
        printCodedString();
    }

    private void printCodedString() {
        StringBuilder codedString = new StringBuilder();
        ArrayList<Character> codedList = new ArrayList<>();
        for (char z : initialString.toCharArray()) {
            codedString.append(codedValues.get(z));
        }
        for (char z : codedString.toString().toCharArray()) {
            codedList.add(codedList.size(), z);
        }
        StringBuilder newCodedString = new StringBuilder();
        for (char z : codedList) {
            newCodedString.append(z);
        }

        codedBytesString = newCodedString.toString();
    }

    public String getString() {
        return codedBytesString;
    }

    public String initialStringToByteString(String initial) {
        StringBuilder result = new StringBuilder();
        for (byte b : initial.getBytes(StandardCharsets.UTF_8)) {
            result.append(Integer.toBinaryString(b));
        }
        return result.toString();
    }

    public String getCodes() {
        Iterator it = codedValues.entrySet().iterator();
        StringBuilder codes = new StringBuilder();
        while (it.hasNext()) {
            Map.Entry element = (Map.Entry) it.next();
            codes.append(element.getKey());
            codes.append("=");
            codes.append(element.getValue());
            codes.append("|");
        }
        return codes.toString();
    }

    public String decode(String postTextBytes) throws IOException {
        System.out.println("Decoding...");

        Map<Character, String> codeValues = new HashMap<>();
        String byteString = Files.readAllLines(Paths.get(postTextBytes), StandardCharsets.UTF_8).toString();
        byteString = byteString.substring(1, byteString.length() - 1);
        boolean hasCodeValues = true;

        while (hasCodeValues) {
            hasCodeValues = false;
            if (byteString.contains("|")) {
                hasCodeValues = true;
                codeValues.put(byteString.charAt(0), byteString.substring(2, byteString.indexOf("|")));
                byteString = byteString.substring(byteString.indexOf("|") + 1);
            }
        }

        codeValues = sortCodes(codeValues);

        Iterator it = codeValues.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Character, String> element = (Map.Entry<Character, String>) it.next();
            while (byteString.contains(element.getKey().toString())) {
                byteString = byteString.replace(element.getValue(), element.getKey().toString());
            }
        }

        StringBuilder result = new StringBuilder();
        int counter = 1;
        while (byteString.length()!=0){
            String sub = byteString.substring(0,counter);
            for(Map.Entry<Character, String> element : codeValues.entrySet()){
                if (sub.equals(element.getValue())){
                    result.append(element.getKey());
                    byteString = byteString.substring(counter);
                    counter = 0;
                }
            }
            counter++;
        }

        System.out.println("Done");
        return result.toString();
    }

    private HashMap<Character, String> sortCodes(Map<Character, String> map) {
        List<Map.Entry<Character, String>> list = new LinkedList<>(map.entrySet());

        list.sort(Comparator.comparingInt(o -> o.getValue().length()));
        Collections.reverse(list);

        HashMap<Character, String> temp = new LinkedHashMap<>();
        for (Map.Entry<Character, String> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

}