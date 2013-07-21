/*  Copyright (C) 2003-2011 JabRef contributors.
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/
package net.sf.jabref.imports;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.sf.jabref.BibtexEntry;
import net.sf.jabref.OutputPrinter;

/**
 * Role of an importer for JabRef.
 * 
 * <p>Importers are sorted according to following criteria
 * <ol><li>
 *   custom importers come first, then importers shipped with JabRef
 * </li><li>
 *   then importers are sorted by name.
 * </li></ol>
 * </p>
 */
public abstract class ImportFormat implements Comparable<ImportFormat> {

    private boolean isCustomImporter;
    
    /**
     * Constructor for custom importers.
     */
    public ImportFormat() {
      this.isCustomImporter = false;
    }

    /**
     * Check whether the source is in the correct format for this importer.
     */
    public abstract boolean isRecognizedFormat(InputStream in) throws IOException;

    /**
     * Parse the entries in the source, and return a List of BibtexEntry
     * objects.
     *
     * This method can be called in two different contexts - either when importing in
     * a specified format, or when importing in unknown format. In the latter case,
     * JabRef cycles through all available import formats. No error messages or feedback
     * is displayed from individual import formats in this case.
     *
     * If importing in a specified format, and null or an empty list is returned, JabRef reports
     * that no entries were found. If an IOException is thrown, JabRef displays the exception's
     * message in unmodified form.
     */
    public abstract List<BibtexEntry> importEntries(InputStream in, OutputPrinter status) throws IOException;


    /**
     * Name of this import format.
     * 
     * <p>The name must be unique.</p>
     * 
     * @return format name, must be unique and not <code>null</code>
     */
    public abstract String getFormatName();
    
    /**
     * Extensions that this importer can read.
     * 
     * @return comma separated list of extensions or <code>null</code> for the default
     */
    public String getExtensions() {
      return null;
    }
    
    /**
     * Short, one token ID to identify the format from the command line.
     * 
     * @return command line ID
     */
    public String getCLIId() {
      String id = getFormatName();
      StringBuffer result = new StringBuffer(id.length());
      for (int i = 0; i < id.length(); i++) {
        char c = id.charAt(i);
        if (Character.isLetterOrDigit(c)) {
          result.append(Character.toLowerCase(c));
        }
      }
      return result.toString();
    }
    
    /**
     * Description  of the ImportFormat.
     * 
     * <p>Implementors of ImportFormats should override this. Ideally, it should specify
     * <ul><li>
     *   what kind of entries from what sources and based on what specification it is able to import
     * </li><li>
     *   by what criteria it {@link #isRecognizedFormat(InputStream) recognizes} an import format
     * </li></ul>
     * 
     * @return description of the import format
     */
    public String getDescription() {
      return "No description available for " + getFormatName() + ".";
    }
    
    /**
     * Sets if this is a custom importer.
     * 
     * <p>For custom importers added dynamically to JabRef, this will be
     * set automatically by JabRef.</p>
     * 
     * @param isCustomImporter if this is a custom importer
     */
    public final void setIsCustomImporter(boolean isCustomImporter) {
      this.isCustomImporter = isCustomImporter;
    }
    
    /**
     * Wether this importer is a custom importer.
     * 
     * <p>Custom importers will have precedence over built-in importers.</p>
     * 
     * @return  wether this is a custom importer
     */
    public final boolean getIsCustomImporter() {
      return this.isCustomImporter; 
    }
        
    /*
     *  (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
      return getFormatName().hashCode();
    }
    
    /*
     *  (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
      return o != null 
          && o instanceof ImportFormat
          && ((ImportFormat)o).getIsCustomImporter() == getIsCustomImporter() 
          && ((ImportFormat)o).getFormatName().equals(getFormatName());
    }
    
    /*
     *  (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
      return getFormatName();
    }
    
    /*
     *  (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(ImportFormat importer) {
      int result = 0;
      if (getIsCustomImporter() == importer.getIsCustomImporter()) {
        result = getFormatName().compareTo(importer.getFormatName());
      } else {
        result = getIsCustomImporter() ? 1 : -1;
      }
      return result;
    }
}