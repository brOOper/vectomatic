/*
Canvas for GWT
Copyright (C) 2006 Alexei Sokolov http://gwt.components.googlepages.com/

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA

*/

package com.gwt.components.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.MouseWheelListener;
import com.google.gwt.user.client.ui.MouseWheelListenerCollection;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.SourcesMouseWheelEvents;
import com.google.gwt.user.client.ui.impl.FocusImpl;

public class Canvas extends FocusWidget implements 
SourcesMouseEvents, SourcesMouseWheelEvents {

    public static class DrawingStyle extends JavaScriptObject {
        protected DrawingStyle() {
            super();
        }
    }

    public static class Gradient extends DrawingStyle {
    	protected Gradient() {
            super();
        }

        protected static native void addColorStop(JavaScriptObject obj,
                float offset, String color) /*-{
         obj.addColorStop(offset, color);
        }-*/;
    }

    public static class LinearGradient extends Gradient {
    	protected LinearGradient() {
            super();
        }

        public final LinearGradient addColorStop(float offset, String color) {
            Gradient.addColorStop(this, offset, color);
            return this;
        }
    }

    public static class RadialGradient extends Gradient {
    	protected RadialGradient() {
            super();
        }

        public final RadialGradient addColorStop(float offset, String color) {
            Gradient.addColorStop(this, offset, color);
            return this;
        }
    }

    public static class Pattern extends DrawingStyle {
        protected Pattern() {
            super();
        }

    }

    private JavaScriptObject context;
    private Element canvas;
    protected int modifiers;
    private MouseListenerCollection mouseListeners;
    private MouseWheelListenerCollection mouseWheelListeners;
    
    public Canvas(int width, int height) {
        setElement(DOM.createDiv());
        sinkEvents(Event.MOUSEEVENTS|Event.KEYEVENTS|Event.ONMOUSEWHEEL);
        canvas = DOM.createElement("canvas");
        setWidth(width);
        setHeight(height);
        DOM.appendChild(getElement(), canvas);
        setStyleName("gwt-Canvas");
		Element focusable = FocusImpl.getFocusImplForPanel().createFocusable();
		DOM.appendChild(getElement(), focusable);

        init();

        setFillStyle("black");
        setStrokeStyle("black");
    }
    
    public int getWidth() {
    	return DOM.getIntAttribute(canvas, "width");
    }
    public void setWidth(int width) {
        DOM.setAttribute(canvas, "width", String.valueOf(width));
    }
    public int getHeight() {
    	return DOM.getIntAttribute(canvas, "height");
    }    
    public void setHeight(int height) {
        DOM.setAttribute(canvas, "height", String.valueOf(height));
    }
    
    public void resize(int width, int height) {
        setWidth(width);
        setHeight(height);
		setSize(width + "px", height + "px");
    }
        
    public native static boolean isEmulation() /*-{
     return (typeof $wnd.G_vmlCanvasManager != "undefined");
    }-*/;

    protected native void init() /*-{
     var el = this.@com.google.gwt.user.client.ui.UIObject::getElement()().firstChild;
     if (typeof $wnd.G_vmlCanvasManager != "undefined") {
       var parent = el.parent;
     
       el = $wnd.G_vmlCanvasManager.fixElement_(el);
       el.getContext = function () {
         if (this.context_) {
           return this.context_;
         }
         return this.context_ = new $wnd.CanvasRenderingContext2D(el);
       };

       el.attachEvent("onpropertychange", function (e) {
         // we need to watch changes to width and height
         switch (e.propertyName) {
           case "width":
           case "height":
             // coord size changed?
             break;
           }
       });

       // if style.height is set

       var attrs = el.attributes;
       if (attrs.width && attrs.width.specified) {
         // TODO: use runtimeStyle and coordsize
         // el.getContext().setWidth_(attrs.width.nodeValue);
         el.style.width = attrs.width.nodeValue + "px";
       }
       if (attrs.height && attrs.height.specified) {
         // TODO: use runtimeStyle and coordsize
         // el.getContext().setHeight_(attrs.height.nodeValue);
         el.style.height = attrs.height.nodeValue + "px";
       }
     }
     this.@com.gwt.components.client.Canvas::context = el.getContext("2d");
    }-*/;
    


	public void addMouseListener(MouseListener listener) {
		if (mouseListeners == null) {
			mouseListeners = new MouseListenerCollection();
		}
		mouseListeners.add(listener);
	}

	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
			case Event.ONMOUSEDOWN:
			case Event.ONMOUSEUP:
			case Event.ONMOUSEMOVE:
			case Event.ONMOUSEOVER:
			case Event.ONMOUSEOUT: {
				if (mouseListeners != null) {
					modifiers = (DOM.eventGetShiftKey(event) ? KeyboardListener.MODIFIER_SHIFT : 0)
					  | (DOM.eventGetMetaKey(event) ? KeyboardListener.MODIFIER_META : 0)
				      | (DOM.eventGetCtrlKey(event) ? KeyboardListener.MODIFIER_CTRL : 0)
				      | (DOM.eventGetAltKey(event) ? KeyboardListener.MODIFIER_ALT : 0);
					mouseListeners.fireMouseEvent(this, event);
				}
				break;
			}
			case Event.ONMOUSEWHEEL: {
				if (mouseWheelListeners != null) {
					modifiers = (DOM.eventGetShiftKey(event) ? KeyboardListener.MODIFIER_SHIFT : 0)
					  | (DOM.eventGetMetaKey(event) ? KeyboardListener.MODIFIER_META : 0)
				      | (DOM.eventGetCtrlKey(event) ? KeyboardListener.MODIFIER_CTRL : 0)
				      | (DOM.eventGetAltKey(event) ? KeyboardListener.MODIFIER_ALT : 0);
					mouseWheelListeners.fireMouseWheelEvent(this, event);
				}
				break;
			}
			case Event.ONKEYDOWN:
			case Event.ONKEYPRESS:
			case Event.ONKEYUP:
				super.onBrowserEvent(event);
		}
	}

	public void removeMouseListener(MouseListener listener) {
		if (mouseListeners != null) {
			mouseListeners.remove(listener);
		}
	}
	
	public int getModifiers() {
		return modifiers;
	}

	public void addMouseWheelListener(MouseWheelListener listener) {
		if (mouseWheelListeners == null) {
			mouseWheelListeners = new MouseWheelListenerCollection();
		}
		mouseWheelListeners.add(listener);
	}

	public void removeMouseWheelListener(MouseWheelListener listener) {
		if (mouseWheelListeners != null) {
			mouseWheelListeners.remove(listener);
		}
	}

    public native void saveContext() /*-{
     this.@com.gwt.components.client.Canvas::context.save();
    }-*/;

    public native void restoreContext() /*-{
     this.@com.gwt.components.client.Canvas::context.restore();
    }-*/;

    public native void scale(float x, float y) /*-{
     this.@com.gwt.components.client.Canvas::context.scale(x, y);   
    }-*/;

    public native void rotate(float angle)/*-{
     this.@com.gwt.components.client.Canvas::context.rotate(angle);   
    }-*/;

    public native void translate(float x, float y) /*-{
     this.@com.gwt.components.client.Canvas::context.translate(x, y);   
    }-*/;

    public native void transform(float m11, float m12, float m21, float m22,
            float dx, float dy) /*-{
     this.@com.gwt.components.client.Canvas::context.transform(
      m11, m12, m21, m22, dx, dy);   
    }-*/;

    public native void setTransform(float m11, float m12, float m21, float m22,
            float dx, float dy) /*-{
     this.@com.gwt.components.client.Canvas::context.setTransform(
      m11, m12, m21, m22, dx, dy);   
    }-*/;

    public native float getGlobalAlpha() /*-{
     return this.@com.gwt.components.client.Canvas::context.globalAlpha;
    }-*/;

    public native void setGlobalAlpha(float alpha) /*-{
     this.@com.gwt.components.client.Canvas::context.globalAlpha = alpha;
    }-*/;

    public native String getGlobalCompositeOperation() /*-{
     return this.@com.gwt.components.client.Canvas::context.globalCompositeOperation;
    }-*/;

    public native void setGlobalCompositeOperation(String operation) /*-{
     this.@com.gwt.components.client.Canvas::context.globalCompositeOperation = 
      operation;
    }-*/;

    public native Object getStrokeStyle() /*-{
    return this.@com.gwt.components.client.Canvas::context.strokeStyle;
	}-*/;

    public native Object getFillStyle() /*-{
        return this.@com.gwt.components.client.Canvas::context.fillStyle;
    }-*/;

    /**
     * @param style
     * Can be either a java.lang.String valued to a valid CSS color or
     * a DrawingStyle
     */
    public native void setStrokeStyle(String style) /*-{
     this.@com.gwt.components.client.Canvas::context.strokeStyle = style;
    }-*/;
    public native void setStrokeStyle(DrawingStyle style) /*-{
    this.@com.gwt.components.client.Canvas::context.strokeStyle = style;
   }-*/;

    /**
     * @param style
     * Can be either a java.lang.String valued to a valid CSS color or
     * a DrawingStyle
     */
    public native void setFillStyle(String style) /*-{
     this.@com.gwt.components.client.Canvas::context.fillStyle = style;
    }-*/;
    public native void setFillStyle(DrawingStyle style) /*-{
     this.@com.gwt.components.client.Canvas::context.fillStyle = style;
    }-*/;

    public native LinearGradient createLinearGradient(float x0, float y0,
            float x1, float y1) /*-{
     return this.@com.gwt.components.client.Canvas::context.createLinearGradient(
      x0, y0, x1, y1);   
    }-*/;

    public native RadialGradient createRadialGradient(float x0, float y0,
            float r0, float x1, float y1, float r1) /*-{
     return this.@com.gwt.components.client.Canvas::context.createRadialGradient(
      x0, y0, r0, x1, y1, r1);   
    }-*/;

    public native Pattern createPattern(Image img, String repetition) /*-{
     var elem = img.@com.google.gwt.user.client.ui.UIObject::getElement()();
     var ctx = this.@com.gwt.components.client.Canvas::context;
     if (ctx.createPattern)
     return ctx.createPattern(elem, repetition);
     return null;   
    }-*/;

    public native float getLineWidth() /*-{
     return this.@com.gwt.components.client.Canvas::context.lineWidth;
    }-*/;

    public native void setLineWidth(float lineWidth) /*-{
     this.@com.gwt.components.client.Canvas::context.lineWidth = lineWidth;
    }-*/;

    public native String getLineCap() /*-{
     return this.@com.gwt.components.client.Canvas::context.lineCap;
    }-*/;

    public native void setLineCap(String lineCap) /*-{
     this.@com.gwt.components.client.Canvas::context.lineCap = lineCap;
    }-*/;

    public native String getLineJoin() /*-{
     return this.@com.gwt.components.client.Canvas::context.lineJoin;
    }-*/;

    public native void setLineJoin(String lineJoin) /*-{
     this.@com.gwt.components.client.Canvas::context.lineJoin = lineJoin;
    }-*/;

    public native float getMiterLimit() /*-{
     return this.@com.gwt.components.client.Canvas::context.miterLimit;
    }-*/;

    public native void setMiterLimit(float miterLimit) /*-{
     this.@com.gwt.components.client.Canvas::context.miterLimit = miterLimit;
    }-*/;

    public native float getShadowOffsetX() /*-{
     return this.@com.gwt.components.client.Canvas::context.shadowOffsetX;
    }-*/;

    public native void setShadowOffsetX(float x) /*-{
     this.@com.gwt.components.client.Canvas::context.shadowOffsetX = x;
    }-*/;

    public native float getShadowOffsetY() /*-{
     return this.@com.gwt.components.client.Canvas::context.shadowOffsetY;
    }-*/;

    public native void setShadowOffsetY(float y) /*-{
     this.@com.gwt.components.client.Canvas::context.shadowOffsetY = y;
    }-*/;

    public native float getShadowBlur() /*-{
     return this.@com.gwt.components.client.Canvas::context.shadowBlur;
    }-*/;

    public native void setShadowBlur(float blur) /*-{
     this.@com.gwt.components.client.Canvas::context.shadowBlur = blur;
    }-*/;

    public native String getShadowColor() /*-{
     return this.@com.gwt.components.client.Canvas::context.shadowColor;
    }-*/;

    public native void setShadowColor(String style) /*-{
     this.@com.gwt.components.client.Canvas::context.shadowColor = style;
    }-*/;

    public native void clearRect(float x, float y, float w, float h) /*-{
     var ctx = this.@com.gwt.components.client.Canvas::context;
     if (typeof $wnd.G_vmlCanvasManager != "undefined") {
       var el = this.@com.google.gwt.user.client.ui.UIObject::getElement()();
       if (el.currentStyle) {
         var color = el.currentStyle['background-color'];
         if (!color) {
           color = '#ffffff';
         }
         ctx.save();
         ctx.fillStyle = color;
         ctx.fillRect(x, y, w, h);
         ctx.restore();
       }
     } else {
       ctx.clearRect(x, y, w, h);
     }
    }-*/;

    public native void fillRect(float x, float y, float w, float h) /*-{
     this.@com.gwt.components.client.Canvas::context.fillRect(x, y, w, h);
    }-*/;

    public native void strokeRect(float x, float y, float w, float h) /*-{
     this.@com.gwt.components.client.Canvas::context.strokeRect(x, y, w, h);
    }-*/;

    public native void beginPath() /*-{
     this.@com.gwt.components.client.Canvas::context.beginPath();
    }-*/;

    public native void closePath() /*-{
     this.@com.gwt.components.client.Canvas::context.closePath();
    }-*/;

    public native void moveTo(float x, float y) /*-{
     this.@com.gwt.components.client.Canvas::context.moveTo(x,y);
    }-*/;

    public native void lineTo(float x, float y) /*-{
     this.@com.gwt.components.client.Canvas::context.lineTo(x,y);
    }-*/;

    public native void quadraticCurveTo(float cpx, float cpy, float x, float y) /*-{
     this.@com.gwt.components.client.Canvas::context.quadraticCurveTo(cpx, cpy, x, y);
    }-*/;

    public native void bezierCurveTo(float cp1x, float cp1y, float cp2x,
            float cp2y, float x, float y) /*-{
     this.@com.gwt.components.client.Canvas::context.bezierCurveTo(
      cp1x, cp1y, cp2x, cp2y, x, y);
    }-*/;

    public native void arcTo(float x1, float y1, float x2, float y2,
            float radius) /*-{
     this.@com.gwt.components.client.Canvas::context.arcTo(x1, y1, x2, y2, radius);
    }-*/;

    public native void rect(float x, float y, float w, float h) /*-{
     this.@com.gwt.components.client.Canvas::context.rect(x, y, w, h);
    }-*/;

    public native void arc(float x, float y, float radius, float startAngle,
            float endAngle, boolean anticlockwise) /*-{
     this.@com.gwt.components.client.Canvas::context.arc(
      x, y, radius, startAngle, endAngle, anticlockwise);
    }-*/;

    public native void fill() /*-{
     this.@com.gwt.components.client.Canvas::context.fill();
    }-*/;

    public native void stroke() /*-{
     this.@com.gwt.components.client.Canvas::context.stroke();
    }-*/;

    public native void clip() /*-{
     this.@com.gwt.components.client.Canvas::context.clip();
    }-*/;

    public native boolean isPointInPath(float x, float y) /*-{
     return this.@com.gwt.components.client.Canvas::context.isPointInPath(x, y);
    }-*/;

}
