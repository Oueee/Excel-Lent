package util;

import java.lang.StringBuilder;
import java.io.File;

public class PathUtils {
    /**
	 * Iterates over strings to create a complete path from a base one
	 * 
	 * @param File
	 * @param strings
	 * @return File
	 */
	 
    public static File join(final File base, final String ... pathElements)
    {
        File result = null;
        
        if (pathElements == null || pathElements.length == 0)
            result = new File(base.getAbsolutePath());
        else
        {
            final StringBuilder builder;

            builder = new StringBuilder();

            for(final String pathElement : pathElements)
            {
                builder.append(File.separator);
                builder.append(pathElement);
            }

            result = new File(base, builder.toString());
        }

        return result;
    }
}