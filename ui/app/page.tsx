"use client";

import { ProcessSteps } from "@/components/process-steps";
import { Terminal } from "@/components/terminal";
import { useState } from "react";
import { toast, ToastContainer } from "react-toastify";

export default function Home() {
  const [activeStep, setActiveStep] = useState(1);
  const [folderPath, setFolderPath] = useState('');
  const [logs, setLogs] = useState<string[]>([]);

  const startProcessing = async () => {

    if (folderPath == '' || folderPath.trim().length == 0) {
      toast.error('Invalid Folder Path',);
      return;
    }

    const res = await fetch('http://localhost:8080/api/comments/', {
      method: 'POST',
      body: JSON.stringify({ path: folderPath }),
      headers: { 'Content-Type': 'application/json' }
    });

    const { jobId } = await res.json();

    const eventSource = new EventSource(`http://localhost:8080/api/comments/sse/${jobId}`);

    eventSource.onmessage = (event) => {
      const data = JSON.parse(event.data);
      setLogs(prev => [...prev, data.message]);

      if (data.type === "PROJECT_SCANNED")
        setActiveStep(2);
      if (data.type === "GIT_ENVIRONMENT_READY")
        setActiveStep(3);
      if (data.type === "PROCESSING")
        setActiveStep(4);


      if (data.type == "COMPLETED") {
        setActiveStep(5);
        eventSource.close();
      }

      // console.log(data);
    }

  }

  return (
    <>

      <main className="max-w-5xl mx-auto mt-20 p-6 bg-card border border-border rounded-2xl shadow-xl grid grid-cols-12 gap-8">
        <ProcessSteps currentStep={activeStep} />{/* Steps */}

        <section className="col-span-8 flex flex-col gap-6">

          {
            activeStep == 1 ? (
              <div className="space-y-4">
                <h2 className="text-xl font-bold text-foreground">Select Project</h2>
                <input
                  className="w-full p-3 bg-background border border-border rounded-md text-foreground placeholder:text-zinc-500 focus:ring-2 focus:ring-primary outline-none transition-all"
                  placeholder="Paste absolute project path..."
                  onChange={(e) => setFolderPath(e.target.value)}
                />
                <button
                  onClick={startProcessing}
                  className="w-full bg-primary text-white py-3 rounded-md font-bold hover:brightness-110 active:scale-[0.98] transition-all"
                >
                  Start Processing
                </button>
              </div>
            ) : (
              <>
                <h2 className="text-lg font-bold flex justify-between items-center text-foreground">
                  Processing Pipeline
                  {activeStep < 4 && (
                    <span className="text-xs text-primary animate-pulse font-mono">
                      Running...
                    </span>
                  )}
                </h2>
                <Terminal logs={logs} />
              </>
            )
          }
        </section>

      </main>
      {/* Container */}

      <ToastContainer pauseOnFocusLoss={false}
        position="top-center" hideProgressBar={true} theme="dark" />
    </>
  );
}
