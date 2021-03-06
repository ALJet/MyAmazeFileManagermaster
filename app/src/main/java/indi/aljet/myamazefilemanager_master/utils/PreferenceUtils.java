package indi.aljet.myamazefilemanager_master.utils;

import android.graphics.Color;

/**
 * Created by PC-LJL on 2018/1/18.
 */

public class PreferenceUtils {

    public static final String KEY_PRIAMRY_TWO = "skin_two";
    public static final String KEY_PRIMARY = "skin";
    public static final String KEY_ACCENT = "accent_skin";
    public static final String KEY_ICON_SKIN = "icon_skin";
    public static final String KEY_CURRENT_TAB = "current_tab";
    public static final String KEY_ROOT = "rootmode";
    public static final String KEY_PATH_COMPRESS = "zippath";

    public static final int DEFAULT_PRIMARY = 4;
    public static final int DEFAULT_ACCENT = 1;
    public static final int DEFAULT_ICON = -1;
    public static final int DEFAULT_CURRENT_TAB = 1;

    public static int getStatusColor(String skin) {
        return darker(Color.parseColor(skin));
    }

    public static int getStatusColor(int skin) {
        return darker(skin);
    }


    private static int darker(int color){
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a,Math.max((int) (r * 0.6f),0),
                Math.max((int) (g * 0.6f ),0),
                Math.max((int) (b * 0.6f),0));
    }


    public static final String LICENCE_TERMS =

            "<html><body>" +
                    "<h3>Notices for files:</h3>" +
                    "<ul><li>nineoldandroids-2.4.0.jar</ul></li>" +	//nineoldandroids
                    "<p style = 'background-color:#eeeeee;padding-left:1em'><code>" +
                    "<br>/*<br>" +
                    "&nbsp;* Copyright 2012 Jake Wharton<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* Licensed under the Apache License, Version 2.0 (the \"License\");<br>" +
                    "&nbsp;* you may not use this file except in compliance with the License.<br>" +
                    "&nbsp;* You may obtain a copy of the License at<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* &nbsp;&nbsp;&nbsp;http://www.apache.org/licenses/LICENSE-2.0<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* Unless required by applicable law or agreed to in writing, software<br>" +
                    "&nbsp;* distributed under the License is distributed on an \"AS IS\" BASIS,<br>" +
                    "&nbsp;* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>" +
                    "&nbsp;* See the License for the specific language governing permissions and<br>" +
                    "&nbsp;* limitations under the License.<br>" +
                    "&nbsp;*/ " +
                    "<br><br></code></p>" +
                    "<h3>Notices for libraries:</h3> " +
                    "<ul><li>libsuperuser</ul></li>" +	//libsupersu
                    "<p style = 'background-color:#eeeeee;padding-left:1em'><code>" +
                    "<br>/*<br>" +
                    "&nbsp;*                                  Apache License<br>" +
                    "&nbsp;*                            Version 2.0, January 2004<br>" +
                    "&nbsp;*                         http://www.apache.org/licenses/<br>" +
                    "&nbsp;*/ " +
                    "<br><br></code></p>" +
                    "<h3>Notices for libraries:</h3> " +
                    "<ul><li>CircularImageView</ul></li>" +	//CircularImageView
                    "<p style = 'background-color:#eeeeee;padding-left:1em'><code>" +
                    "<br>/*<br>" +
                    "&nbsp;* The MIT License (MIT)<br>" +
                    "&nbsp;*<br>" +
                    "&nbsp;* Copyright (c) 2014 Pkmmte Xeleon<br>" +
                    "&nbsp;*<br>" +
                    "&nbsp;* Permission is hereby granted, free of charge, to any person obtaining a copy<br>" +
                    "&nbsp;* of this software and associated documentation files (the \"Software\"), to deal<br>" +
                    "&nbsp;* in the Software without restriction, including without limitation the rights<br>" +
                    "&nbsp;* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell<br>" +
                    "&nbsp;* copies of the Software, and to permit persons to whom the Software is<br>" +
                    "&nbsp;* furnished to do so, subject to the following conditions:" +
                    "&nbsp;*<br>" +
                    "&nbsp;* The above copyright notice and this permission notice shall be included in<br>" +
                    "&nbsp;* all copies or substantial portions of the Software.<br>" +
                    "&nbsp;* THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR<br>" +
                    "&nbsp;* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,<br>" +
                    "&nbsp;* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE<br>" +
                    "&nbsp;* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER<br>" +
                    "&nbsp;* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,<br>" +
                    "&nbsp;* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN<br>" +
                    "&nbsp;* THE SOFTWARE.<br>" +
                    "&nbsp;*/ " +
                    "<br><br></code></p>" +
                    "<h3>Notices for libraries:</h3> " +
                    "<ul><li>FloatingActionButton</ul></li>" +	//FloatingActionBar
                    "<p style = 'background-color:#eeeeee;padding-left:1em'><code>" +
                    "<br>/*<br>" +
                    "&nbsp;* The MIT License (MIT)<br>" +
                    "&nbsp;*<br>" +
                    "&nbsp;* Copyright (c) 2014 Oleksandr Melnykov<br>" +
                    "&nbsp;*<br>" +
                    "&nbsp;* Permission is hereby granted, free of charge, to any person obtaining a copy<br>" +
                    "&nbsp;* of this software and associated documentation files (the \"Software\"), to deal<br>" +
                    "&nbsp;* in the Software without restriction, including without limitation the rights<br>" +
                    "&nbsp;* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell<br>" +
                    "&nbsp;* copies of the Software, and to permit persons to whom the Software is<br>" +
                    "&nbsp;* furnished to do so, subject to the following conditions:" +
                    "&nbsp;*<br>" +
                    "&nbsp;* The above copyright notice and this permission notice shall be included in<br>" +
                    "&nbsp;* all copies or substantial portions of the Software.<br>" +
                    "&nbsp;* THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR<br>" +
                    "&nbsp;* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,<br>" +
                    "&nbsp;* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE<br>" +
                    "&nbsp;* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER<br>" +
                    "&nbsp;* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,<br>" +
                    "&nbsp;* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN<br>" +
                    "&nbsp;* THE SOFTWARE.<br>" +
                    "&nbsp;*/ " +
                    "<br><br></code></p>" +
                    "<h3>Notices for libraries:</h3>" +
                    "<ul><li>Android System Bar Tint</ul></li>" +	// SystemBar tint
                    "<p style = 'background-color:#eeeeee;padding-left:1em'><code>" +
                    "<br>/*<br>" +
                    "&nbsp;* Copyright 2013 readyState Software Limited<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* Licensed under the Apache License, Version 2.0 (the \"License\");<br>" +
                    "&nbsp;* you may not use this file except in compliance with the License.<br>" +
                    "&nbsp;* You may obtain a copy of the License at<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* &nbsp;&nbsp;&nbsp;http://www.apache.org/licenses/LICENSE-2.0<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* Unless required by applicable law or agreed to in writing, software<br>" +
                    "&nbsp;* distributed under the License is distributed on an \"AS IS\" BASIS,<br>" +
                    "&nbsp;* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>" +
                    "&nbsp;* See the License for the specific language governing permissions and<br>" +
                    "&nbsp;* limitations under the License.<br>" +
                    "&nbsp;*/ " +
                    "<br><br></code></p>" +
                    "<h3>Notices for libraries:</h3> " +
                    "<ul><li>Material Dialogs</ul></li>" +	//Material Dialogs
                    "<p style = 'background-color:#eeeeee;padding-left:1em'><code>" +
                    "<br>/*<br>" +
                    "&nbsp;* The MIT License (MIT)<br>" +
                    "&nbsp;*<br>" +
                    "&nbsp;* Copyright (c) 2014 Aidan Michael Follestad<br>" +
                    "&nbsp;*<br>" +
                    "&nbsp;* Permission is hereby granted, free of charge, to any person obtaining a copy<br>" +
                    "&nbsp;* of this software and associated documentation files (the \"Software\"), to deal<br>" +
                    "&nbsp;* in the Software without restriction, including without limitation the rights<br>" +
                    "&nbsp;* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell<br>" +
                    "&nbsp;* copies of the Software, and to permit persons to whom the Software is<br>" +
                    "&nbsp;* furnished to do so, subject to the following conditions:" +
                    "&nbsp;*<br>" +
                    "&nbsp;* The above copyright notice and this permission notice shall be included in<br>" +
                    "&nbsp;* all copies or substantial portions of the Software.<br>" +
                    "&nbsp;* THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR<br>" +
                    "&nbsp;* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,<br>" +
                    "&nbsp;* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE<br>" +
                    "&nbsp;* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER<br>" +
                    "&nbsp;* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,<br>" +
                    "&nbsp;* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN<br>" +
                    "&nbsp;* THE SOFTWARE.<br>" +
                    "&nbsp;*/ " +
                    "<br><br></code></p>" +
                    "<h3>Notices for libraries:</h3>" +
                    "<ul><li>UnRAR</ul></li>" +	// junRar
                    "<p style = 'background-color:#eeeeee;padding-left:1em'><code>" +
                    "<br>/*<br>" +
                    "&nbsp;* UnRAR - free utility for RAR archives<br>" +
                    "&nbsp;* License for use and distribution of<br>" +
                    "&nbsp;* FREE portable version<br>" +
                    "&nbsp;*/ " +
                    "<br><br>" +
                    "https://raw.githubusercontent.com/junrar/junrar/master/license.txt" +
                    "<br><br></code></p>" +
                    "<h3>Notices for libraries:</h3>" +
                    "<ul><li>commons-compress</ul></li>" +	// commons-compress
                    "<p style = 'background-color:#eeeeee;padding-left:1em'><code>" +
                    "<br>/*<br>" +
                    "&nbsp;* Copyright [yyyy] [name of copyright owner]<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* Licensed under the Apache License, Version 2.0 (the \"License\");<br>" +
                    "&nbsp;* you may not use this file except in compliance with the License.<br>" +
                    "&nbsp;* You may obtain a copy of the License at<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* &nbsp;&nbsp;&nbsp;http://www.apache.org/licenses/LICENSE-2.0<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* Unless required by applicable law or agreed to in writing, software<br>" +
                    "&nbsp;* distributed under the License is distributed on an \"AS IS\" BASIS,<br>" +
                    "&nbsp;* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>" +
                    "&nbsp;* See the License for the specific language governing permissions and<br>" +
                    "&nbsp;* limitations under the License.<br>" +
                    "&nbsp;*/ " +
                    "<br><br></code></p>" +
                    "<h3>Notices for libraries:</h3>" +
                    "<ul><li>sticky-headers-recyclerview</ul></li>" +	// stickyHeadersRecyclerView
                    "<p style = 'background-color:#eeeeee;padding-left:1em'><code>" +
                    "<br>/*<br>" +
                    "&nbsp;* Copyright 2014 Jacob Tabak - Timehop<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* Licensed under the Apache License, Version 2.0 (the \"License\");<br>" +
                    "&nbsp;* you may not use this file except in compliance with the License.<br>" +
                    "&nbsp;* You may obtain a copy of the License at<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* &nbsp;&nbsp;&nbsp;http://www.apache.org/licenses/LICENSE-2.0<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* Unless required by applicable law or agreed to in writing, software<br>" +
                    "&nbsp;* distributed under the License is distributed on an \"AS IS\" BASIS,<br>" +
                    "&nbsp;* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>" +
                    "&nbsp;* See the License for the specific language governing permissions and<br>" +
                    "&nbsp;* limitations under the License.<br>" +
                    "&nbsp;*/ " +
                    "<br><br></code></p>" +
                    "<h3>Notices for libraries:</h3>" +
                    "<ul><li>JCIFS</ul></li>" +	// jcifs
                    "<p style = 'background-color:#eeeeee;padding-left:1em'><code>" +
                    "<br>/*<br>" +
                    "&nbsp;* Copyright (C) <year>  <name of author><br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* This library is free software; you can redistribute it and/or<br>" +
                    "&nbsp;* modify it under the terms of the GNU Lesser General Public<br>" +
                    "&nbsp;* License as published by the Free Software Foundation; either<br>" +
                    "&nbsp;* version 2.1 of the License, or (at your option) any later version.<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* This library is distributed in the hope that it will be useful,<br>" +
                    "&nbsp;* but WITHOUT ANY WARRANTY; without even the implied warranty of<br>" +
                    "&nbsp;* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU<br>" +
                    "&nbsp;* Lesser General Public License for more details.<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* You should have received a copy of the GNU Lesser General Public<br>" +
                    "&nbsp;* License along with this library; if not, write to the Free Software<br>" +
                    "&nbsp;* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA<br>" +
                    "&nbsp;*/ " +
                    "<br><br></code></p>" +
                    "<h3>Notices for libraries:</h3>" +
                    "<ul><li>Apache Mina</li> <li>Apache FtpServer</li></ul>" +	// apache
                    "<p style = 'background-color:#eeeeee;padding-left:1em'><code>" +
                    "<br>/*<br>" +
                    "&nbsp;* Copyright [yyyy] [name of copyright owner]<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* Licensed under the Apache License, Version 2.0 (the \"License\");<br>" +
                    "&nbsp;* you may not use this file except in compliance with the License.<br>" +
                    "&nbsp;* You may obtain a copy of the License at<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* &nbsp;&nbsp;&nbsp;http://www.apache.org/licenses/LICENSE-2.0<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* Unless required by applicable law or agreed to in writing, software<br>" +
                    "&nbsp;* distributed under the License is distributed on an \"AS IS\" BASIS,<br>" +
                    "&nbsp;* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>" +
                    "&nbsp;* See the License for the specific language governing permissions and<br>" +
                    "&nbsp;* limitations under the License.<br>" +
                    "&nbsp;*/ " +
                    "<br><br></code></p>" +
                    "<h3>Notices for libraries:</h3>" +
                    "<ul><li>MPAndroidChart</ul></li>" +	// MPAndroidChart
                    "<p style = 'background-color:#eeeeee;padding-left:1em'><code>" +
                    "<br>/*<br>" +
                    "&nbsp;* Copyright 2016 Philipp Jahoda<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* Licensed under the Apache License, Version 2.0 (the \"License\");<br>" +
                    "&nbsp;* you may not use this file except in compliance with the License.<br>" +
                    "&nbsp;* You may obtain a copy of the License at<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* &nbsp;&nbsp;&nbsp;http://www.apache.org/licenses/LICENSE-2.0<br>" +
                    "&nbsp;* <br>" +
                    "&nbsp;* Unless required by applicable law or agreed to in writing, software<br>" +
                    "&nbsp;* distributed under the License is distributed on an \"AS IS\" BASIS,<br>" +
                    "&nbsp;* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>" +
                    "&nbsp;* See the License for the specific language governing permissions and<br>" +
                    "&nbsp;* limitations under the License.<br>" +
                    "&nbsp;*/ " +
                    "<br><br></code></p>" +
                    "</body></html>";




}
