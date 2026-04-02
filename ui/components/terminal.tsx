export const Terminal = ({ logs }: { logs: string[] }) => (
  <div className="bg-black text-green-500 p-4 rounded-lg h-64 overflow-y-auto font-mono text-xs shadow-inner">
    {logs.length === 0 && <span className="text-gray-600 italic">No events received yet...</span>}
    {logs.map((log, i) => (
      <div key={i} className="mb-1">
        <span className="text-blue-400 mr-2">[{new Date().toLocaleTimeString()}]</span>
        {`> ${log}`}
      </div>
    ))}
  </div>
);