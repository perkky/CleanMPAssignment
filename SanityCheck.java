


/*****************************************************
 *
 * SanityCheck class
 * Inherits from: nil
 * Purpose: Preforms the neccessary sanity checks r
            required to make the data assignment-ready.
            No major work is done here, mostly format 
            fixing.
 *
 *****************************************************/


//Class which contains static functions used to sanity check various things
public class SanityCheck
{
    //Sanity checks the word against all words in fileLocation, seperated by a line
    //It does this by looking for the longest common subsequence.
    //      If the word is 3 or lessletters long, LCS must equal the word size
    //      If the word is between 4 and 6 letters, LCS must be greater than the word size -1
    //      If the word is above 7 letters, LCS must be greater than the word size -2
    //This function also fixes so misc formatting such as NONFLAMMABLE to NON-FLAMMABLE
    //Basically just makes the words match what is needed for the assingment spec, not too much correcting is preformed

    //The format for file is:
    //  word to match,E - optional if the word bust be exact, if any words past this point have already been matched this word is ignored.
    public static String sanityCheckText(String word, String fileLocation1, String fileLocation2)
    {
        FileIO sanityWords = new FileIO(fileLocation1);
        String newWord = "";

        //First check if any of the words match any in the dictionary file
        while (!sanityWords.isEmpty())
        {
            String line = sanityWords.getLine();
            String[] readWords = line.split(",");
            int lcs = getLCS(word, readWords[0]);
            boolean added = false;


            //A special case of when the word contains RADIOACTIVE needs to be taken into account,
            // as it is usually RADIOACTRIVE I/II/III (although not always)
            if (!word.contains("RADIOACTIVE"))
            {

                //If the words is 3 or less characters or if it requires it to be exact
                //only add it if it is exact
                if ((readWords.length ==2) || readWords[0].length() < 4)
                {
                    
                    if (readWords.length >= 2)
                    {
                        //must be exact if it has E as the second word
                        if (readWords[1].equals("E"))
                        {
                            if (word.contains(readWords[0]))
                            {
                                newWord += readWords[0] + " ";
                                added = true;
                            }
                        }
                        else
                        {
                            //If theres no E, search to see if the words have been matched before
                            //As well as seeing if the word is in the string
                            boolean add = true;
                            if (!word.contains(readWords[0]))
                                add = false;
                            for (int i = 1; i < readWords.length; i++)
                                if ( newWord.contains(readWords[i]) )  
                                    add = false;

                            if (add)
                            {
                                newWord += readWords[0] + " ";
                                added = true;
                            }
                        }
                    }
                    else
                    {
                        if (word.contains(readWords[0]))
                            {
                                newWord += readWords[0] + " ";
                                added = true;
                            }
                    }
                }
                if ( !added && readWords[0].length() >3 && ((readWords[0].length() < 7 && lcs >=  readWords[0].length()-1) || (lcs >=  readWords[0].length()-2)))
                {
                    //do nothing if the second word is contained
                    if (readWords.length >= 2 && !readWords[1].equals("E"))
                    {
                        boolean add = true;
                        for (int i =1; i < readWords.length; i++)
                            if ( newWord.contains(readWords[i]) )  
                                add = false;

                        if (add)
                            newWord += readWords[0] + " ";
                    }
                    else if (readWords.length >=2 && readWords[1].equals("E"))
                    {
                        if (word.contains(readWords[0]))
                        {
                            newWord += readWords[0] + " ";
                        }
                    }
                    else
                        newWord += readWords[0] + " ";
                }
            }
            else
            {
                newWord = word.replace("RADIOACTIVE", "RADIOACTIVE " );
            }

        }
        if (newWord.equals(""))
            newWord = word;

        sanityWords.close();

        //Finally replace the words with their propper words (eg NONFLAMMABLE with NON-FLAMMABLE)
        FileIO correctWords = new FileIO(fileLocation2);
        
        while (!correctWords.isEmpty())
        {
            String line = correctWords.getLine();
            String[] words = line.split(",");
            
            newWord = newWord.replace(words[0], words[1]);
        }
        
        return newWord;
    }

    //https://www.programcreek.com/2014/04/longest-common-subsequence-java/
    public static int getLCS(String a, String b)
    {
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m+1][n+1];
    
        for(int i=0; i<=m; i++){
            for(int j=0; j<=n; j++){
                if(i==0 || j==0){
                    dp[i][j]=0;
                }else if(a.charAt(i-1)==b.charAt(j-1)){
                    dp[i][j] = 1 + dp[i-1][j-1];
                }else{
                    dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
                }
            }
        }
    
        return dp[m][n];
    }

    //This function just properly formats the symbol based on the file
    //Inputed symbols are in the form of symbol.png
    //Will find the corresponding name in file
    //The format of the file is:
    //  symbol.png,Proper Symbol Name
    public static String sanityCheckSymbol(String symbol, String fileLocation)
    {
        String newSymbol = "";

        FileIO sanitySymbols = new FileIO(fileLocation);

        while (!sanitySymbols.isEmpty())
        {
            String line = sanitySymbols.getLine();
            String[] words = line.split(",");

            if (symbol.equals(words[0]))
            {
                newSymbol = words[1];
                break;
            }
        }
        sanitySymbols.close();
        return newSymbol;
    }
}