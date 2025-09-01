import { post } from "./common.js";
const today = new Date().toISOString().slice(0,10);
post("/v1/acquiring/recon/get", { reconDate: today, settlePeriod: "day" }).then(console.log).catch(e => console.error(e.response?.data || e));
