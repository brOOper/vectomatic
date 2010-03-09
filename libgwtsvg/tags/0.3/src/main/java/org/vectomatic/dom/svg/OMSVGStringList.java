/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of libgwtsvg.
 * 
 * libgwtsvg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * libgwtsvg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with libgwtsvg.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.dom.svg;

import com.google.gwt.core.client.JavaScriptObject;

public class OMSVGStringList extends JavaScriptObject {
  protected OMSVGStringList() {
  }

  // Implementation of the svg::SVGStringList W3C IDL interface
  public final native int getNumberOfItems() /*-{
    return this.numberOfItems;
  }-*/;
  public final native void clear() /*-{
    this.clear();
  }-*/;
  public final native String initialize(String newItem) /*-{
    return this.initialize(newItem);
  }-*/;
  public final native String getItem(int index) /*-{
    return this.getItem(index);
  }-*/;
  public final native String insertItemBefore(String newItem, int index) /*-{
    return this.insertItemBefore(newItem, index);
  }-*/;
  public final native String replaceItem(String newItem, int index) /*-{
    return this.replaceItem(newItem, index);
  }-*/;
  public final native String removeItem(int index) /*-{
    return this.removeItem(index);
  }-*/;
  public final native String appendItem(String newItem) /*-{
    return this.appendItem(newItem);
  }-*/;

}
