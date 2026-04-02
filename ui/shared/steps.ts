import { CheckCircle, Folder, GitBranch, Loader, LucideIcon, Search } from 'lucide-react';

export interface Step {
  id: number,
  name: string,
  icon: LucideIcon
}

export const STEPS: Step[] = [
  {
    id: 1,
    name: 'Select Project',
    icon: Folder
  },
  {
    id: 2,
    name: 'Scan Files',
    icon: Search
  },
  {
    id: 3,
    name: 'Git Environment',
    icon: GitBranch
  },
  {
    id: 4,
    name: 'Processing',
    icon: Loader
  },
  {
    id: 5,
    name: 'Completed',
    icon: CheckCircle
  },
];