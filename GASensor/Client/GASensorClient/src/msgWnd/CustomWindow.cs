using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;

namespace GASensorClient
{
    using WND_PROC_LISTENER_TYPE = Dictionary<WndProcDelegate, List<int>>;
    using System.Diagnostics;

    delegate void WndProcDelegate(uint msg, IntPtr wParam, IntPtr lParam);

    class CustomWindow : IDisposable
    {
        delegate IntPtr WndProc(IntPtr hWnd, uint msg, IntPtr wParam, IntPtr lParam);        

        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Unicode)]
        struct WNDCLASS
        {
            public uint style;
            public IntPtr lpfnWndProc;
            public int cbClsExtra;
            public int cbWndExtra;
            public IntPtr hInstance;
            public IntPtr hIcon;
            public IntPtr hCursor;
            public IntPtr hbrBackground;
            [MarshalAs(UnmanagedType.LPWStr)]
            public string lpszMenuName;
            [MarshalAs(UnmanagedType.LPWStr)]
            public string lpszClassName;
        }

        [DllImport("user32.dll", SetLastError = true)]
        static extern System.UInt16 RegisterClassW(
            [In] ref WNDCLASS lpWndClass
        );

        [DllImport("user32.dll", SetLastError = true)]
        static extern IntPtr CreateWindowExW(
           UInt32 dwExStyle,
           [MarshalAs(UnmanagedType.LPWStr)]
           string lpClassName,
           [MarshalAs(UnmanagedType.LPWStr)]
           string lpWindowName,
           UInt32 dwStyle,
           Int32 x,
           Int32 y,
           Int32 nWidth,
           Int32 nHeight,
           IntPtr hWndParent,
           IntPtr hMenu,
           IntPtr hInstance,
           IntPtr lpParam
        );

        [DllImport("user32.dll", SetLastError = true)]
        static extern System.IntPtr DefWindowProcW(
            IntPtr hWnd, uint msg, IntPtr wParam, IntPtr lParam
        );

        [DllImport("user32.dll", SetLastError = true)]
        static extern bool DestroyWindow(
            IntPtr hWnd
        );

        private const int ERROR_CLASS_ALREADY_EXISTS = 1410;

        private bool disposed = false;
        private IntPtr hwnd;

        public static WND_PROC_LISTENER_TYPE eventListeners = new WND_PROC_LISTENER_TYPE();

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        private void Dispose(bool disposing)
        {
            if (!disposed)
            {
                if (disposing)
                {
                    // Dispose managed resources
                }

                // Dispose unmanaged resources
                if (hwnd != IntPtr.Zero)
                {
                    DestroyWindow(hwnd);
                    hwnd = IntPtr.Zero;
                }
            }
        }

        public CustomWindow(string className, WndProcDelegate procDelegate, List<int> regEvents)
        {
            if (className == null) throw new System.Exception("class_name is null");
            if (className == String.Empty) throw new System.Exception("class_name is empty");

            if (procDelegate != null)
            {
                eventListeners.Add(procDelegate, regEvents);
            }

            wndProcDelegate = CustomWndProc;

            // Create WNDCLASS
            WNDCLASS wind_class = new WNDCLASS();
            wind_class.lpszClassName = className;
            wind_class.lpfnWndProc = Marshal.GetFunctionPointerForDelegate(wndProcDelegate);            

            UInt16 class_atom = RegisterClassW(ref wind_class);

            int last_error = Marshal.GetLastWin32Error();

            if (class_atom == 0 && last_error != ERROR_CLASS_ALREADY_EXISTS)
            {
                throw new System.Exception("Could not register window class");
            }

            // Create window
            hwnd = CreateWindowExW(
                0,
                className,
                String.Empty,
                0,
                0,
                0,
                0,
                0,
                IntPtr.Zero,
                IntPtr.Zero,
                IntPtr.Zero,
                IntPtr.Zero
            );
        }
        
        private static IntPtr CustomWndProc(IntPtr hWnd, uint msg, IntPtr wParam, IntPtr lParam)
        {   
            foreach (var listener in CustomWindow.eventListeners)
            {
                foreach (var ev in listener.Value)
                {
                    if (ev == msg)
                    {
                        listener.Key(msg, wParam, lParam);
                    }
                }
            }

            return DefWindowProcW(hWnd, msg, wParam, lParam);
        }

        private WndProc wndProcDelegate;
    }
}
