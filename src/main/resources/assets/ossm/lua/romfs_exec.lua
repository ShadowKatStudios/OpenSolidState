local comp = component or require("component")
local prom = comp.proxy(comp.list("ossm_prom")())
--local gpu = comp.proxy(comp.list("gpu")())

local gpu = comp.list("gpu")()
local w, h

local screen = comp.list('screen')()
for address in comp.list('screen') do
  if #comp.invoke(address, 'getKeyboards') > 0 then
    screen = address
  end
end

local cls = function()end
if gpu and screen then
  comp.invoke(gpu, "bind", screen)
  w, h = comp.invoke(gpu, "getResolution")
  comp.invoke(gpu, "setResolution", w, h)
  comp.invoke(gpu, "setBackground", 0x000000)
  comp.invoke(gpu, "setForeground", 0xFFFFFF)
  comp.invoke(gpu, "fill", 1, 1, w, h, " ")
  cls = function()comp.invoke(gpu,"fill", 1, 1, w, h, " ")end
end
local y = 1
local function status(msg)
  if gpu and screen then
    comp.invoke(gpu, "set", 1, y, msg)
    if y == h then
      comp.invoke(gpu, "copy", 1, 2, w, h - 1, 0, -1)
      comp.invoke(gpu, "fill", 1, h, w, 1, " ")
    else
      y = y + 1
    end
  end
end

status("Unpacking ROM to tmpfs...")

local tmpfs
for e in comp.list("filesystem") do
  if (comp.invoke(e, "getLabel") == "tmpfs") then
    tmpfs = comp.proxy(e)
    break
  end
end

local pos = 1

local function read(n)
  local blkstart = (pos//512)+1
  local blkend = ((pos+n)//512)+1
  local pstart = (pos%512)
  local pend = pstart+n-1
  local buf = ""
  --print(blkstart, blkend)
  for i=blkstart, blkend do
    buf = buf .. assert(prom.blockRead(i))
  end
--  print(buf:sub(pstart, pend))
  pos = pos + n
  return buf:sub(pstart, pend)
end

local function readInt(n)
  local buf = 0
  for i=0, n-1 do
    buf = buf | (read(1):byte() << (i*8))
  end
  return buf
end

local function mkdir(d)
  if (not d) then return end
--  print("DEBUG", d)
  if (d:match("^(.+)/.+$") and not tmpfs.exists(d:match("^(.+)/.+$"))) then
    mkdir(d:match("^(.+)/.+$"))
  end
  tmpfs.makeDirectory(d)
end

if (read(7) ~= "romfs\1\0") then
  error("Not a valid romfs.\n")
  return
end

local lname = ""
while lname ~= "TRAILER!!!" do
  local nsize = readInt(1)
  local name = read(nsize)
  if (name == "TRAILER!!!") then break end
  status("> "..name)
  local fsize = readInt(2)
  local exec = read(1)
  local f = read(fsize)
  mkdir(name:match("^(.+)/.+$"))
  local h = tmpfs.open(name, "w")
  tmpfs.write(h, f)
  tmpfs.close(h)
end

local h = tmpfs.open("init.lua", "r")
local b = ""
local xbuf = ""
status("Loading init...")
while b do
  b = tmpfs.read(h, math.huge)
  if (b) then
    xbuf = xbuf .. b
  end
end
status("Booting.")
function computer.getBootAddress()
  return tmpfs.address
end
return load(xbuf, "=init.lua")()