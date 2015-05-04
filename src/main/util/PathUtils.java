package util;

import java.util.ArrayList;
import java.util.List;

import java.lang.StringBuilder;
import java.io.File;

public class PathUtils {

    /**
	 * Iterates over strings to get a grandchild of the base dir
	 * basically, it just join the differents elements of the path and
	 * create a new File
	 *
	 * @param File
	 * @param strings
	 * @return File
	 */
    public static File child(final File base, final String ... pathElements)
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
                builder.append(pathElement.replaceAll("(\\W)+", "_"));
            }

            result = new File(base, builder.toString());
        }

        return result;
    }

    public static File child_from_list(final File base, final ArrayList<String> pathElements)
    {
        File result = null;

        if (pathElements == null || pathElements.size() == 0)
            result = new File(base.getAbsolutePath());
        else
        {
            final StringBuilder builder;

            builder = new StringBuilder();

            for(final String pathElement : pathElements)
            {
                builder.append(File.separator);
                builder.append(pathElement.replaceAll("(\\W)+", "_"));
            }

            result = new File(base, builder.toString());
        }

        return result;
    }
}
