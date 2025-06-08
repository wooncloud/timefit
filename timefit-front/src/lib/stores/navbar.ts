import { writable } from 'svelte/store';

export const navLeft = writable<string>('');
export const navCenter = writable<string>('');
export const navVisible = writable<boolean>(true);
