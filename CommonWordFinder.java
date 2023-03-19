import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
/**
 * Class with three different methods to find the common words in txt files
 * by implementing the MyMap interface.
 * @author Melisa Zhang mz2955
 * @version 1.0 December 13, 2022
 */

public class CommonWordFinder {
    //set the default limit to equal 10 if no arg[2] is processed
    protected static int limit = 10;

    public static void main(String[] args){
        //create an empty MyMap
        MyMap<String, Integer> map = null;
        //checks to make sure the # of arguments is correct, else print error and exit
        if(args.length<2 || args.length>3){
            System.err.println("Usage: java CommonWordFinder <filename> <bst|avl|hash> [limit]");
            System.exit(1);
        } else{
            //if # of args is correct, continue to check if args are valid
            try{
                //create new file
                File input = new File(args[0]);
                //check to see if the file exists in directory, else throw error
                if(!input.exists()){
                    throw new FileNotFoundException("Cannot open file '" + args[0] + "' for input.");
                }else {
                    //check args[1] to see if arg is a valid data type, else throw error
                    try{
                        if(args[1].equals("bst")){
                            map = new BSTMap<>();
                        }else if(args[1].equals("avl")){
                            map = new AVLTreeMap<>();
                        }else if(args[1].equals("hash")){
                            map = new MyHashMap<>();
                        } else{
                            throw new IllegalArgumentException();
                        }

                        //I am taking the assumption that 0 is not a positive integer! Checks to see if args[2] is a positive integer, else throw error
                        if(args.length!=2){
                            if( Integer.parseInt(args[2]) > 0){
                                limit = Integer.parseInt(args[2]);
                            }else{
                                throw new IllegalArgumentException("Error: Invalid limit '" + args[2] + "' received.");
                            }
                        }
                        //instantiate BufferedReader to read txt file
                        BufferedReader txt = new BufferedReader(new FileReader(input));
                        //initialize string to hold each line of the txt file
                        String string = "";
                        //initialize StringBuilder to hold each individual word
                        StringBuilder word = new StringBuilder();
                        while(string!=null){
                            //checks to see if the txt file line is empty, if it is, continue to next line
                            if (string.isBlank()){
                                string = txt.readLine();
                            }else{
                                //convert line of text all to lowercase
                                string = string.toLowerCase();
                                for(int i = 0; i<string.length();i++){
                                    //checks to see if the character in the string is in the alphabet, if it is append to StringBuilder(word)
                                    if(string.charAt(i)>=97 && string.charAt(i)<=122){
                                        word.append(string.charAt(i));
                                        //checks to see if the character is a "'", if it is, append it. From my understanding,
                                        //even if it is a single quote by itself, it would still be appended as a word. Edstem #2641
                                    } else if(string.charAt(i) == '\''){
                                        word.append(string.charAt(i));
                                        //checks to see if character is a "-", if it is, append it
                                    } else if(string.charAt(i) == '-'){
                                        word.append(string.charAt(i));
                                        //checks to see if character is a " "
                                    } else if(string.charAt(i) == ' '){
                                        //checks to see if the word begins with a non-alphabetical character or a "'", if it doesn't, delete the character
                                        if(word.length()!=0 && word.charAt(0)=='-'){
                                            while(word.length()!=0 && (word.charAt(0)<97 || word.charAt(0)>122)){
                                                word.delete(0,1);
                                            }
                                        }
                                        //if the word exists put it into the map
                                        if(word.length()!=0){
                                            if(map.get(word.toString())==null){
                                                map.put(word.toString(),1);
                                            } else{
                                                map.put(word.toString(),map.get(word.toString())+1);
                                            }
                                        }
                                        //reset the StringBuilder so that we can move on to the next word in the line
                                        word.setLength(0);
                                    }
                                }
                                //adds whatever is in the StringBuilder when a space does not end the line, in other words,
                                //it adds the last word of every line to the map
                                if(!word.isEmpty()){
                                    //checks the same conditions when a word is added when a " " is encountered
                                    if(word.length()!=0 && word.charAt(0)== '-'){
                                        while(word.length()!=0 && word.charAt(0)=='-'){
                                            word.delete(0,1);
                                        }
                                    }
                                    if(word.length()!=0){
                                        if(map.get(word.toString())==null){
                                            map.put(word.toString(),1);
                                        } else{
                                            map.put(word.toString(),map.get(word.toString())+1);
                                        }
                                    }
                                    //resets StringBuilder
                                    word.setLength(0);
                                }
                                //move on to next line in the txt file
                                string = txt.readLine();
                            }
                        }

                        //create a 2d Object array of arrays that holds two values
                        Object[][] arr = new Object[map.size()][2];
                        int index  = 0;
                        //add every entry in the map to the 2d array
                        Iterator<Entry<String,Integer>> iter = map.iterator();
                        while(iter.hasNext()){
                            Entry<String,Integer> temp = iter.next();
                            //the first value in the array will be the word frequency
                            arr[index][0] = temp.value;
                            //the second value in the array will be the word
                            arr[index][1] = temp.key;
                            index++;
                        }

                        //if the file is empty, throw IOException
                        if(input.length()==0){
                            throw new IOException();
                        }
                        //if there are no words, print that there are no words and exit successfully
                        if(map.size()==0){
                            System.out.println("Total unique words: " + map.size());
                            System.exit(0);
                        }

                        //find the longest word in the map
                        Iterator<Entry<String,Integer>> iter2 = map.iterator();
                        Entry<String,Integer> temp = iter2.next();
                        String longestWord = temp.key;
                        while(iter2.hasNext()){
                            temp = iter2.next();
                            if(temp.key.length()>longestWord.length()){
                                longestWord = temp.key;
                            }
                        }
                        //sort the array alphabetically first
                        Arrays.sort(arr,(a,b) -> CharSequence.compare((String)a[1],(String)b[1]));
                        //sort the array by word frequency
                        Arrays.sort(arr, (a, b) -> Integer.compare((int)b[0],(int)a[0]));

                        //the number of unique words in the map
                        int maxNumSize = Integer.toString(map.size()).length();
                        //this is the list number, so like 1., 2., 3., etc.
                        int counter = 1;
                        //counter to keep track of how many words and limits to print out
                        int max = 0;
                        System.out.println("Total unique words: " + map.size());
                        while(max<map.size() && max<limit){
                            //subtracts the current counter's length from the total amount of words that will be displayed to make the counter right-aligned
                            if(limit<map.size()){
                                for(int i = 0; i<Integer.toString(limit).length()-Integer.toString(counter).length();i++){
                                    System.out.print(" ");
                                }
                            }else{
                                for(int i = 0; i<maxNumSize-Integer.toString(counter).length();i++){
                                    System.out.print(" ");
                                }
                            }
                            //print the counter
                            System.out.print(counter + ". ");
                            //print the word
                            System.out.print(arr[max][1]);
                            String currentWord = (String)arr[max][1];
                            //find the number of spaces to be added to every word to make it the same length as the longest word
                            for(int i = 0; i<longestWord.length()-currentWord.length();i++){
                                System.out.print(" ");
                            }
                            //add a space and then print the word frequency
                            System.out.println(" " + arr[max][0]);
                            System.lineSeparator();
                            counter++;
                            max++;
                        }
                    } catch(IOException e){
                        //prints error message if something goes wrong while reading the txt file and exits in failure
                        System.err.println("Error: An I/O error occurred reading '"+ args[0] + "'.");
                        System.exit(1);
                    }
                }
            } catch(IndexOutOfBoundsException e){
                //prints error message if wrong number of arguments is entered and exits in failure
                System.err.println("Usage: java CommonWordFinder <filename> <bst|avl|hash> [limit]");
                System.exit(1);
            } catch(FileNotFoundException e){
                //prints error message if the file does not exist in the directory and exits in failure
                System.err.println("Error: Cannot open file '" + args[0] + "' for input.");
                System.exit(1);
            } catch (IllegalArgumentException e) {
                //prints error message if invalid arguments are entered and exits in failure
                if(!args[1].equals("bst")&&!args[1].equals("avl")&&!args[1].equals("hash")){
                    System.err.println("Error: Invalid data structure '" + args[1] + "' received.");
                }else{
                    System.err.println("Error: Invalid limit '" + args[2] + "' received.");
                }
                System.exit(1);
            }
        }
    }
}