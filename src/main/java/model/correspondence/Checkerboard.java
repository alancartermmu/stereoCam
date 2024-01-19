// TODO - javadoc
// TODO - unit tests

package model.correspondence;

import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;

public class Checkerboard
{
    /*---------
     * FIELDS *
     ---------*/
    
    private int nRows;
    private int nColumns;
    private double squareSize; // millimetres

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public Checkerboard(int nRows, int nColumns, double squareSize)
    {
        this.nRows = nRows;
        this.nColumns = nColumns;
        this.squareSize = squareSize;
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public int Rows() { return nRows; }
    public void setnRows(int rows) { this.nRows = rows; }
    public int Columns() { return nColumns; }
    public void setnColumns(int columns) { this.nColumns = columns; }
    public double SquareSize() { return squareSize; }
    public void setSquareSize(double squareSize) { this.squareSize = squareSize; }

    /*----------
     * METHODS *
     ----------*/

    public int nPointsX() { return this.nColumns - 1; }

    public int nPointsY() { return this.nRows - 1; }

    public int nPoints() { return this.nPointsX() * this.nPointsY(); }

    public Size PatternSize()
    {
        return new Size(nPointsX(), nPointsY());
    }

    // Generate container of all internal points
    public MatOfPoint3f ObjectPoints()
    {
        // World co-ord system: origin at top-left *internal* corner (as Matlab), X→ Y↓ (coplanar with board), Z = 0

        // Create temporary array
        Point3[] points = new Point3[nPoints()];

        // Add internal points to list
        for (int i = 0; i < nPoints(); i++)
        {
            // Number of squares between origin and point
            int nX = i % (nColumns - 1);
            int nY = i / (nColumns - 1);  // note integer division

            // Co-ordinates of point
            double x = squareSize * nX;
            double y = squareSize * nY;
            double z = 0;

            points[i] = new Point3(x,y,z);
        }

        // Convert array to Mat and return
        return new MatOfPoint3f(points);
    }
}
