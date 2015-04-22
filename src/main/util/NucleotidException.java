package util;

public class NucleotidException extends Exception
{

    public NucleotidException()
    {
        super("An other letter than A C T or G have been found in the genome");
    }
}
