import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import org.opencv.core.Core;

public class Main
{
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    public static void main(String args[])
    {
        
        long startTime = System.currentTimeMillis();

        for (String arg : args)
        {

            Picture pic = new Picture(arg);

            List<Label> labelsList = new ArrayList<>();

            //Find the gaussian blur which provides the greatest number of lables
            //Any incorrectly found labels are dealt with later.
            int maxLabels = 0;
            double maxSigma = 1.0;
            for (double sigma = 0.0; sigma < 2.5; sigma += 0.25)
            {
                pic.setSigma(sigma);
                labelsList = pic.getLabels();

                if (labelsList.size() > maxLabels)
                {
                    maxSigma = sigma;
                    maxLabels = labelsList.size();
                }
            }

            pic.setSigma(maxSigma);
            labelsList = pic.getLabels();
            Label[] labelsArray = labelsList.toArray(new Label[0]);

            //Start each label detecting and analysing its self
            //Each label is run on its own thread
            for (int i = 0; i < labelsArray.length; i++)
            {
                labelsArray[i].setThreadName(Integer.toString(i));
                labelsArray[i].start();
                //labelsArray[i].detect();
                //labelsArray[i].detectSymbol();
            }

            //make sure all the multithreading is finished before continuing
            for (int i = 0; i < labelsArray.length; i++)
            {
				try {
				while (!labelsArray[i].finished) { Thread.sleep(20);} 
				} catch (Exception e) {}
				
				labelsArray[i].setText(SanityCheck.sanityCheckText(labelsArray[i].getText(), "Data/dictionary.data", "Data/correctwords.data"));
				labelsArray[i].setSymbol(SanityCheck.sanityCheckSymbol(labelsArray[i].getSymbol(), "Data/symbols.data"));
            }

            sortLabels(labelsArray);
            System.out.println(arg);
            for (Label label : labelsArray)
            {
                if (!label.getSymbol().equals(""))
                {
                    System.out.println("Top:\t" + label.getTopColour());
                    System.out.println("Bot:\t" + label.getBotColour());
                    System.out.println("Class:\t" + label.getClassNum());
                    System.out.println("Text:\t" + label.getText());//label.getText());  
                    System.out.println("Symbol:\t" + label.getSymbol());
                    System.out.println();
                }

                //System.out.println(OCR.sanityCheck(label.getText(), "words.txt"));
                //System.out.println(label.getText());

                //label.showImage("label");

                //label.release();
            }
            
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Took "+ (endTime - startTime) + " ms total");
		System.out.println("Took "+(endTime - startTime)/args.length + " ms per image");
    }
    
    public static void sortLabels(Label[] labels)
    {
        for (int i = 0; i < labels.length-1; i++)
        {
            for (int j = 0; j < labels.length-1; j++)
            {
                boolean switched = false;
                if (isAlphabetical(labels[j+1].getTopColour(), labels[j].getTopColour()))
                {
                        switched = true;
                }
                else if (labels[j+1].getTopColour().equals(labels[j].getTopColour()))
                {
                        if (isAlphabetical(labels[j+1].getBotColour(), labels[j].getBotColour()))
                        {
                                switched = true;
                        }
                        else if (labels[j+1].getBotColour().equals(labels[j].getBotColour()))
                        {
                                if (isAlphabetical(labels[j+1].getClassNum(), labels[j].getClassNum()))
                                {
                                        switched = true;
                                }
                                else if (labels[j+1].getClassNum().equals(labels[j].getClassNum()))
                                {
                                        if (isAlphabetical(labels[j+1].getText(), labels[j].getText()))
                                        {
                                                switched = true;
                                        }
                                        else if (labels[j+1].getText().equals(labels[j].getText()))
                                        {
                                                if (isAlphabetical(labels[j+1].getSymbol(), labels[j].getSymbol()))
                                                {
                                                        switched = true;
                                                }
                                                else if (labels[j+1].getSymbol().equals(labels[j].getSymbol()))
                                                {
                                                
                                                }
                                        }
                                }
                        }
                }
                
                if (switched)
                {
                        Label temp = labels[j];
                        labels[j] = labels[j+1];
                        labels[j+1] = temp;
                }
            }
        }
    }
    
    public static boolean isAlphabetical(String str1, String str2)
	{
		boolean result = false;
		boolean found = false;
		int i = 0;
		int minLen = getMinStringLen(str1, str2);

		while(i < minLen && found == false)
		{
			if(str1.charAt(i) == str2.charAt(i))
			{
				i++;
			}
			else if(str1.charAt(i) < str2.charAt(i))
			{
				result = true;
				found = true;
			}
			else
			{
				found = true;
			}
		}

		if(found == false && str1.length() < str2.length())
		{
			result = true;
		}

		return result;
	}

	public static int getMinStringLen(String str1, String str2)
	{
		int minLen;

		if(str2.length() > str1.length())
		{
			minLen = str1.length();
		}
		else
		{
			minLen = str2.length();
		}

		return minLen;
	}

}
