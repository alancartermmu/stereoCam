package model.correspondence;

import org.opencv.core.Size;
import org.opencv.core.TermCriteria;

public class SearchParams
{
    /*---------
     * FIELDS *
     ---------*/
    
    private int searchWinSize = 10;  // convolution PatternSize
    private int searchZeroZone = -1;
    private int termMaxCount = 30;
    private double termEpsilon = 0.1;
    
    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public SearchParams(int searchWinSize, int searchZeroZone, int termMaxCount, double termEpsilon)
    {
        this.searchWinSize = searchWinSize;
        this.searchZeroZone = searchZeroZone;
        this.termMaxCount = termMaxCount;
        this.termEpsilon = termEpsilon;
    }

    public SearchParams(){}
    
    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/
    
    public int SearchWinSize() { return searchWinSize; }
    public void setSearchWinSize(int searchWinSize) { this.searchWinSize = searchWinSize; }
    public int SearchZeroZone() { return searchZeroZone; }
    public void setSearchZeroZone(int searchZeroZone) { this.searchZeroZone = searchZeroZone; }
    public int TermMaxCount() { return termMaxCount; }
    public void setTermMaxCount(int termMaxCount) { this.termMaxCount = termMaxCount; }
    public double TermEpsilon() { return termEpsilon; }
    public void setTermEpsilon(double termEpsilon) { this.termEpsilon = termEpsilon; }
    
    /*----------
     * METHODS *
     ----------*/
    
    public TermCriteria TermCriteria()
    {
        return new TermCriteria(TermCriteria.COUNT + TermCriteria.EPS, termMaxCount, termEpsilon);
    }

    public Size WinSize()
    {
        return new Size(searchWinSize, searchWinSize);
    }

    public Size ZeroZone()
    {
        return new Size(searchZeroZone, searchZeroZone);
    }
}
