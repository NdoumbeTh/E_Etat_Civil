export type Role = 'CITOYEN' | 'OFFICIER' | 'CHEF_SERVICE' | 'ADMIN';

export interface LoginResponse {
  token: string;
  role: Role;
}
