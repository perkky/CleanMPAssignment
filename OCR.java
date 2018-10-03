import net.sourceforge.tess4j.*;

import org.opencv.core.Mat;
import org.opencv.features2d.*;


/*****************************************************
 *
 * OCR class
 * Inherits from: nil
 * Purpose: Preforms OCR on an inputted Mat

 *
 *****************************************************/

public class OCR
{
    private ITesseract instance;

    public OCR()
    {
        instance = new Tesseract();
    }

    //Preforms the OCR on the mat by calling ITesseract.doOCR()
    //Returns a string containing all identified letters/numbers
    public String doOCR(Mat mat)
    {
        String s = "";
        try
        {
            s = instance.doOCR(FileIO.matToBufferedImage(mat));
        }catch (Exception e) { }

        return cleanTextUp(s);
    }

    //This function cleans the text up by removing any non neccessary charcters.
    private String cleanTextUp(String s)
    {
        String newString = "";

        for (int i = 0; i < s.length(); i++)
        {
            //if its a ' ` or -, replace it with a . as the OCR commonly picks these characters up by mistake.
            //This is safe to do as '`- are not used in this case (- is but it is dealt with later)
            if ( (int)s.charAt(i) == 39 || (int)s.charAt(i) == 45 || (int)s.charAt(i) == 96 || (int)s.charAt(i) == 46)
            {
                if (i != 0 )
                {
                    if ((int)newString.charAt(newString.length()-1) > 47 && (int)newString.charAt(newString.length()-1) < 58 ) //replace ' with . as it mistakes the two
                    {
                         newString +=".";
                    }
                }
            }
            //Eemove any useless characters
            //Eg any character that is not alphanumeric or .
            else if ( ((int)s.charAt(i) > 47 && (int)s.charAt(i) < 58) || ( (int)s.charAt(i) > 64 && (int)s.charAt(i) < 91) || ( (int)s.charAt(i) > 96 && (int)s.charAt(i) < 123) || (int)s.charAt(i) == 32)
                newString += s.charAt(i);
        }

        return newString;
    }

    

}