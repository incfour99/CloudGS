using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using NativeMethods;

namespace GASensorClient
{
    using WND_PROC_LISTENER_TYPE = Dictionary<WndProcDelegate, List<int>>;    

    delegate void WndProcDelegate(uint msg, IntPtr wParam, IntPtr lParam);

    class CustomWindow : IDisposable
    {
        delegate IntPtr WndProc(IntPtr hWnd, uint msg, IntPtr wParam, IntPtr lParam);        

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
                    WinAPI.DestroyWindow(hwnd);
                    hwnd = IntPtr.Zero;
                }
            }
        }

        public CustomWindow(string className, WndProcDelegate procDelegate, List<int> regEvents)
        {
            if (className == null) 
                throw new System.Exception("class_name is null");

            if (className == String.Empty) 
                throw new System.Exception("class_name is empty");

            if (procDelegate != null)
            {
                eventListeners.Add(procDelegate, regEvents);
            }

            wndProcDelegate = CustomWndProc;

            // Create WNDCLASS
            WinAPI.WNDCLASS wind_class = new WinAPI.WNDCLASS();
            wind_class.lpszClassName = className;
            wind_class.lpfnWndProc = Marshal.GetFunctionPointerForDelegate(wndProcDelegate);

            UInt16 class_atom = WinAPI.RegisterClassW(ref wind_class);

            int last_error = Marshal.GetLastWin32Error();

            if (class_atom == 0 && last_error != ERROR_CLASS_ALREADY_EXISTS)
            {
                throw new System.Exception("Could not register window class");
            }

            // Create window
            hwnd = WinAPI.CreateWindowExW(
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

            return WinAPI.DefWindowProcW(hWnd, msg, wParam, lParam);
        }

        private WndProc wndProcDelegate;
    }
}
